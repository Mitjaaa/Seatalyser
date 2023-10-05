from django.shortcuts import render
from django.http import HttpResponse
from django.template import loader
from django.template.defaulttags import register

import requests
import json

def index(request):
    response = json.loads(requests.get("https://www.bahn.de/web/api/gsd/api/wagentypen/EPA_353").text)
    carriages = response['wagenteile'][0]
    elements = carriages['elemente']

    width = carriages['width']
    height = carriages['height']


    # for seat in elements:
    #     if seat['type'] == 'PLATZ':
    #         print(seat['nummer'])

    with open('./static/assets/static_data.json') as reservation_file:
        file_contents = reservation_file.read()

    reservations = json.loads(file_contents)
    # print(reservations['carriage'][0]['seats'])

    is_mobile = request.user_agent.is_mobile
    context = {
        "seats": elements,
        "reservations": reservations['carriage'][0]['seats'],
        "width": height * 10 + 40 if is_mobile else width * 10,
        "height":  width * 10 if is_mobile else height * 10 + 40,
    }

    return render(request, "reservations.html", context)


@register.simple_tag
def get_x(list, index, request):
    if request.user_agent.is_mobile:
        return fetch_y(list, index)
    else:
        return fetch_x(list, index)

@register.simple_tag
def get_y(list, index, request):
    if request.user_agent.is_mobile:
        return fetch_x(list, index)
    else:
        return fetch_y(list, index)

def fetch_x(list, index):
    return list[index]['x'] * 10

def fetch_y(list, index):
    if list[index]['type'] == "EINBAU":
        return list[index]['y'] * 10 - 20
    else:
        return list[index]['y'] * 10


@register.filter
def get_status(list, index):
    obj = list.get("{}".format(index))
    if obj != None:
        return obj['status']
    else:
        return None

@register.filter
def get_color_for_status(list, index):
    status = get_status(list, index)

    if status == 'AVAILABLE':
        return "#98fb98"
    elif status == 'RESERVED':
        return "#dcdcdc"
    else:
        return "#ffdf00"

@register.filter
def get_table_width(request):
    if request.user_agent.is_mobile:
        return 73
    else:
        return 33

@register.filter
def get_table_height(request):
    if request.user_agent.is_mobile:
        return 33
    else:
        return 73   