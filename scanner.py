#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Fri Oct 25 14:30:16 2019

@author: chen
"""

from urllib.request import urlopen  
from bs4 import BeautifulSoup
import csv
import json

output = {}
output['Projects'] = []

with open('input.csv') as csv_file:
    csv_reader = csv.reader(csv_file, delimiter=',')
    row_count = 0
    for row in csv_reader:
        if row_count == 0:
            print(f'Column names are {", ".join(row)}')
            row_count += 1
        else:
            project_name = row[0]
            gitrepo_link = row[1]
            combined_url = "".join(["https://raw.", gitrepo_link[9:len(gitrepo_link)], "/master/README.md"])
            # open the git repo readme file 
            html = urlopen(combined_url)
            bsyc = BeautifulSoup(html.read(), "lxml")
            readme_content = bsyc.body.p.get_text() 
            
            lines = readme_content.splitlines()
            output['Projects'].append({
                        'Project name': lines[1],
                        'Project problem type': lines[4],
                        'Project industry': lines[7],
                        'Technologies used': lines[10]
                    })
            
with open('output.txt', 'w') as outfile:
    json.dump(output, outfile)