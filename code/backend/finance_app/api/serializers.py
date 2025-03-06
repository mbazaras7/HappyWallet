from .models import *
from rest_framework import serializers

'''Serializer for users'''
class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ['id', 'email', 'full_name', 'date_of_birth']
        read_only_fields = ['id']

'''Serializer for creating users'''
class UserCreateSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ['email', 'full_name', 'date_of_birth', 'password']

    def create(self, validated_data):
        user = User.objects.create_user(
            email=validated_data['email'],
            password=validated_data['password'],
            full_name=validated_data['full_name'],
            date_of_birth=validated_data['date_of_birth']
        )
        return user

'''Serializer for Expenses'''
class ExpenseSerializer(serializers.ModelSerializer):
    class Meta:
        model = Expense
        fields = ['id', 'user', 'name', 'amount', 'category', 'date', 'vendor']
        read_only_fields = ['user']

'''Serializer for receipts'''
class ReceiptSerializer(serializers.ModelSerializer):
    transaction_date = serializers.DateTimeField(format="%d-%m-%Y", required=False) #Formats transaction date
    uploaded_at = serializers.DateTimeField(format="%d-%m-%Y", required=False) #Formats upload date

    class Meta:
        model = Receipt
        fields = ['id', 'user', 'image_url', 'merchant', 'total_amount','transaction_date', 'parsed_items', 'receipt_category', 'uploaded_at']
        read_only_fields = ['user']

'''Serializer for budgets'''
class BudgetSerializer(serializers.ModelSerializer):
    receipts = ReceiptSerializer(many=True, read_only=True) #Includes receipts under the budget
    start_date = serializers.DateField(format="%d-%m-%Y") #Formats start date
    end_date = serializers.DateField(format="%d-%m-%Y") #Formats end date
    filter_categories = serializers.MultipleChoiceField(choices=CategoryChoices.choices) #Budget category filters

    class Meta:
        model = Budget
        fields = ['id', 'user', 'name', 'filter_categories', 'limit_amount', 'current_spending', 'start_date', 'end_date', 'receipts']
        read_only_fields = ['user']

