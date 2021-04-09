from uuid import uuid4

from django.http import HttpResponse
from django.shortcuts import render

from april_web.settings import BASE_DIR
from base.models import User, Issue, IssueStatus


def get_issues(id, limit=100000):
    try:
        user = User.objects.get(id=id)
        issues = Issue.objects.filter(user=user)[:limit:1]
        return issues
    except Exception as e:
        print(e)
        return []


def list_last_issues(request):
    issues = get_issues(request.COOKIES.get("user_id"), 5)
    return render(request, "issues.html",
                  {'issues_count': len(issues), 'last_issues': issues, 'workplace': False, 'authorized': True})


def get_issue_photo(request, w):
    with open("{0}/issue_photo/{1}.png".format(BASE_DIR, w), "rb") as f:
        return HttpResponse(status=200, content=f.read(), content_type="image/png")


def user_page(request):
    issues = get_issues(request.COOKIES.get("user_id"))
    return render(request, "issues.html",
                  {'issues_count': len(issues), 'last_issues': issues, 'workplace': True, 'authorized': True})


def save_photo(request, name):
    photo_id = uuid4()
    with open("{0}/issue_photo/{1}.png".format(BASE_DIR, str(photo_id)), "wb") as f:
        f.write(request.FILES.get(name).read())
    return photo_id


def save_issue(request):
    user = User.objects.get(id=request.COOKIES.get("user_id"))

    photo_id = ""
    if "pet_photo" in request.FILES.keys():
        photo_id = save_photo(request, "pet_photo")

    result_id = ""
    status = IssueStatus.New
    if "status" in request.POST.keys():
        status = int(request.POST.get("status"))

    if status == IssueStatus.Ready.value:
        result_id = save_photo(request, "result_photo")

    if "issue_id" in request.POST.keys():
        id = request.POST.get("issue_id")
        Issue.objects.filter(id=id).update(status=status, result_photo=result_id)
    else:
        issue = Issue.objects.create(id=uuid4(), user=user, name=request.POST.get("name"), pet_photo=photo_id, status=status)
        issue.save()

    # 200
    return HttpResponse(status=200)
