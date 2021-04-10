from django.urls import path, include
from rest_framework import routers, serializers, viewsets

from rest.models import User


class UserSerializer(serializers.ModelSerializer):
    login = serializers.CharField(source='auth.login')
    password = serializers.CharField(source='auth.password')
    first_name = serializers.CharField(source='personal_data.first_name')
    last_name = serializers.CharField(source='personal_data.last_name')
    patronymic = serializers.CharField(source='personal_data.patronymic')
    phone = serializers.CharField(source='personal_data.phone')
    email = serializers.CharField(source='personal_data.email')

    class Meta:
        model = User
        depth = 1
        exclude = ['personal_data', 'auth']

    def update(self, instance, validated_data):
        print(validated_data)
        instance.auth = validated_data.get('auth', instance.auth)
        instance.personal_data.first_name = validated_data.get('personal_data.first_name', instance.personal_data.first_name)
        instance.personal_data.last_name = validated_data.get('personal_data.last_name', instance.personal_data.last_name)
        instance.save()
        return instance


class UserViewSet(viewsets.ModelViewSet):
    queryset = User.objects.all()
    serializer_class = UserSerializer


router = routers.DefaultRouter()
router.register(r'users', UserViewSet)

urlpatterns = [
    path('', include(router.urls)),
    path('api-auth/', include('rest_framework.urls', namespace='rest_framework'))
]
