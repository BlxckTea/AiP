from Variable import *
from Alarm import *
import Measure
import CheckTime

import threading

t1 = threading.Thread(target = CheckTime.main)
t2 = threading.Thread(target = Measure.main)

t1.start()
t2.start()
