from django.urls import path
from django.views.generic import RedirectView

from session1.views import main_page_view, login_view, signup_view, list_issues_view

urlpatterns = [
    path('', main_page_view, name='Главная'),
    path('login', login_view, name='login'),
    path('signup', signup_view, name='signup'),
    path('user/issues', list_issues_view, name='list_issues'),
    path('favicon.ico', RedirectView.as_view(url='static/favicon.png', permanent=True))
]
