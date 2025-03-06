from django.db import models
from django.conf import settings
from django.contrib.auth.models import AbstractUser, BaseUserManager
from django.db.models import Sum, FloatField, Q
from django.db.models.functions import Cast
from django.utils.timezone import now
from multiselectfield import MultiSelectField

class UserManager(BaseUserManager):
    '''Custom user manager that supports email-based authentication.'''

    use_in_migrations = True

    def _create_user(self, email, password, **extra_fields):
        '''Create and save a user with the given email and password.'''
        if not email:
            raise ValueError('The given email must be set')
        email = self.normalize_email(email)
        user = self.model(email=email, **extra_fields)
        user.set_password(password)
        user.save(using=self._db)
        return user

    def create_user(self, email, password=None, **extra_fields):
        '''Create and save a regular user.'''
        extra_fields.setdefault('is_staff', False)
        extra_fields.setdefault('is_superuser', False)
        return self._create_user(email, password, **extra_fields)

    def create_superuser(self, email, password, **extra_fields):
        '''Create and save a superuser.'''
        extra_fields.setdefault('is_staff', True)
        extra_fields.setdefault('is_superuser', True)

        if extra_fields.get('is_staff') is not True:
            raise ValueError('Superuser must have is_staff=True.')
        if extra_fields.get('is_superuser') is not True:
            raise ValueError('Superuser must have is_superuser=True.')

        return self._create_user(email, password, **extra_fields)
    
'''User model using email instead of username.'''
class User(AbstractUser):
    username = None
    id = models.AutoField(primary_key=True)
    email = models.EmailField('Email Address', unique=True, db_index=True)
    full_name = models.CharField(max_length=150, blank=True)
    date_of_birth = models.DateField(blank=True, null=True)

    USERNAME_FIELD = 'email'
    REQUIRED_FIELDS = ['full_name', 'date_of_birth']

    objects = UserManager()
    
    def __str__(self):
        return self.email

'''Category choices for budget, expense and receipts'''
class CategoryChoices(models.TextChoices):
    MEAL = "Meal", "Meal"
    SUPPLIES = "Supplies", "Supplies"
    HOTEL = "Hotel", "Hotel"
    FUEL = "Fuel", "Fuel"
    TRANSPORTATION = "Transportation", "Transportation"
    COMMUNICATION = "Communication", "Communication"
    SUBSCRIPTIONS = "Subscriptions", "Subscriptions"
    ENTERTAINMENT = "Entertainment", "Entertainment"
    TRAINING = "Training", "Training"
    HEALTHCARE = "Healthcare", "Healthcare"
    OTHER = "Other", "Other"

'''Expense Model'''
class Expense(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name="expenses")
    name = models.CharField(max_length=255, null=True, blank=True, default="Expense")
    category = models.CharField(max_length=50, choices=CategoryChoices.choices, default=CategoryChoices.OTHER)
    vendor = models.CharField(max_length=100, blank=True, null=True)
    amount = models.DecimalField(max_digits=10, decimal_places=2)
    date = models.DateField(blank=True, null=True)    
    class Meta:
        verbose_name = "Expense"
        verbose_name_plural = "Expenses"

'''Budget Model with filtered categories and spending limits'''
class Budget(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name="budgets")
    name = models.CharField(max_length=255, null=True, blank=True, default="Budget")
    filter_categories = MultiSelectField(choices=CategoryChoices.choices, blank=True)
    limit_amount = models.DecimalField(max_digits=10, decimal_places=2)
    current_spending = models.DecimalField(max_digits=10, decimal_places=2, default=0)
    start_date = models.DateField()
    end_date = models.DateField()

    def update_spending(self): #Update current spending based on linked receipts totals
        if not self.filter_categories:
            total_spent = (
                self.receipts.exclude(total_amount=None)
                .annotate(total_as_float=Cast("total_amount", FloatField()))
                .aggregate(total=Sum("total_as_float"))['total'] or 0
            )
        else:
            total_spent = (
                self.receipts.filter(receipt_category__in=self.filter_categories)
                .exclude(total_amount=None)
                .annotate(total_as_float=Cast("total_amount", FloatField()))
                .aggregate(total=Sum("total_as_float"))['total'] or 0
            )
        self.current_spending = total_spent
        self.save()

    class Meta:
        verbose_name = "Budget"
        verbose_name_plural = "Budgets"

'''Receipt Model'''
class Receipt(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name="receipts")
    image_url = models.URLField(max_length=500, blank=True, null=True)
    merchant = models.CharField(max_length=100, blank=True, null=True)
    budget = models.ManyToManyField(Budget, related_name="receipts")
    total_amount = models.DecimalField(max_digits=10, decimal_places=2, blank=True, null=True)
    uploaded_at = models.DateTimeField(default=now)
    parsed_items = models.JSONField(blank=True, null=True)
    transaction_date = models.DateTimeField(blank=True, null=True)
    receipt_category = models.CharField(max_length=50, choices=CategoryChoices.choices, default=CategoryChoices.OTHER)
    
    def assign_to_budget(self):#Assign receipt to relevant budgets based on filtered categories and time of receipt
        matching_budgets = Budget.objects.filter(
            user=self.user,
            start_date__lte=self.transaction_date if self.transaction_date else self.uploaded_at,
            end_date__gte=self.transaction_date if self.transaction_date else self.uploaded_at,
        )
        matching_budgets = matching_budgets.filter( #Check if there are any filter categories, if no then add to budget, if yes see if receipt category is one of the filter categories
            Q(filter_categories__isnull=True) |
            Q(filter_categories="") |
            Q(filter_categories__contains=self.receipt_category)   
        )
        
        current_budgets = self.budget.all()
        
        for budget in current_budgets: #Remove from budgets that no longer match
            if budget not in matching_budgets:
                self.budget.remove(budget)
                budget.update_spending()  # Ensure budget reflects changes
            
        if matching_budgets.exists():
            self.budget.set(matching_budgets)
            self.save()
            for budget in matching_budgets:
                budget.update_spending()
    
    def __str__(self):
        return f"Receipt from {self.merchant or 'Unknown Merchant'} uploaded on {self.uploaded_at}"