from django.urls import path, re_path

from user.views import get_issue_photo, user_page, create_issue

urlpatterns = {
    path('', user_page),
    path('save', create_issue),
    path('photo/<w>', get_issue_photo)
}