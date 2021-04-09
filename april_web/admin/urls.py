from django.urls import path

from admin.views import index, find_issue

urlpatterns = [
    path('', index),
    path('issue/<id>', find_issue)
]