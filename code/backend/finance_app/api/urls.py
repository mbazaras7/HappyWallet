from django.urls import path, include
from rest_framework import routers
from api import views
from .views import *
from drf_spectacular.views import SpectacularAPIView, SpectacularRedocView, SpectacularSwaggerView

router = routers.DefaultRouter()
router.register('users', UserViewSet, basename='users')
router.register('expenses', ExpenseViewSet, basename='expenses')
router.register('receipts', ReceiptViewSet, basename='receipts')
router.register('budgets', BudgetViewSet, basename='budgets')


urlpatterns = [
    path('api/', include(router.urls)),
    path('api/schema/', SpectacularAPIView.as_view(), name='schema'),
    path('', SpectacularSwaggerView.as_view(url_name='schema'), name='swagger-ui'),
    path('api/schema/redoc', SpectacularRedocView.as_view(url_name='schema'), name='redoc'),
    path('api/process-receipt/', ProcessReceiptView.as_view(), name='process-receipt'),
    path("api/budget-report/<int:budget_id>/", BudgetReportView.as_view(), name="budget-report"),
    path('login/', EmailPasswordLoginView.as_view(), name='login'),
    path('logout/', LogoutView.as_view(), name='logout'),
    path("api/budget-report/<int:budget_id>/xlsx/", BudgetReportXlsxView.as_view(), name="budget_report_xlsx"),
]