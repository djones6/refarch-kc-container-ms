import csv
import json
from random import gauss
import random
import datetime

containerData = []
def buildJSON(csvfile):
    with open(csvfile) as csvfile:
        dataReader = csv.DictReader(csvfile)
        for row in dataReader:
            x = json.dumps(row)
            containerData.append(x)
        return containerData


with open('container_matrix.csv', mode='w') as container_file:
    container_writer = csv.writer(container_file, delimiter=',', quotechar='"', quoting=csv.QUOTE_MINIMAL, lineterminator='\n')

    container_writer.writerow(['Timestamp', 'ID', 'Temperature(celcius)', 'Target_Temperature(celcius)', 'Amp', 'CumulativePowerConsumption', 'ContentType', 'Humidity', 'CO2', 'Door_Open', 
    'Under Maintainence', 'Defrost_Cycle'])

    #faulty sensor data
    id = random.randint(1001,2000)
    Today= datetime.datetime.today()
    date_list = [Today + datetime.timedelta(minutes=15*x) for x in range(0, 10)]
    print date_list
    for i in range(0, 10):

        timestamp = date_list[i].strftime('%Y-%m-%d T%H:%M Z')
    	container_writer.writerow([timestamp, id, gauss(5.0,5.0), 4.4, gauss(2.5,1.0), gauss(10.0,2.0), random.randint(1,5), 
        gauss(10.5, 5.5), gauss(10.5, 5.0), 'false', 'false', 6])

print(buildJSON('container_matrix.csv'))


