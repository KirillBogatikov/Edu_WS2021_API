from django.urls import path

from base.views import index, login, signup, logout

urlpatterns = [
    path('', index),
    path('login', login),
    path('logout', logout),
    path('signup', signup)
]