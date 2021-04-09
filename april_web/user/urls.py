from django.urls import path

from user.views import get_issue_photo, user_page, save_issue

urlpatterns = {
    path('', user_page),
    path('save', save_issue),
    path('photo/<w>', get_issue_photo)
}