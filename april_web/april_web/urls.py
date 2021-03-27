from django.urls import path, include

urlpatterns = [
    path('', include('base.urls')),
    path('groom/', include('admin.urls')),
    path('user/', include('user.urls')),
    path('issue/', include('user.urls'))
]
