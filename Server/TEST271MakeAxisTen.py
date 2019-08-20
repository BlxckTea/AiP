import numpy as np
from keras.models import load_model

import urllib.request

mean = np.array([0.12706655,0.12859294,0.16698507,0.0062144,0.00590922,0.00854085,0.08114471,0.09077962,1.27170512,0.12729543,0.12769165,0.15860697,0.00560099,0.006132,0.00839742,0.08129832,0.09003703,1.27213745,0.12679698,0.12796326,0.15881856,0.00602722,0.00546672,0.00853882,0.08128204,0.09059753,1.27180684,0.12660166,0.12780151,0.15822856,0.00635986,0.00539347,0.00882568,0.0812383,0.09153748,1.27159017,0.12718557,0.12819824,0.15913798,0.00582682,0.00532735,0.00876363,0.0814153,0.09107666,1.27187195,0.12830048,0.12938334,0.16740189,0.00633748,0.00620728,0.00915731,0.08239593,0.09159749,1.27523168,0.13701808,0.12897746,0.16161601,0.00617269,0.00620321,0.00866292,0.09023259,0.09085999,1.26628927,0.14077174,0.13096008,0.16362915,0.00623881,0.00598653,0.00840861,0.09066493,0.09214681,1.2734731,0.13658981,0.12795003,0.15894674,0.00629781,0.00553996,0.00812174,0.08883286,0.09140727,1.27167053,0.13602117,0.12732035,0.15956828,0.00627136,0.00633545,0.00865377,0.08899358,0.09048462,1.27238871,0.13590113,0.1273763,0.15914917,0.00605876,0.00637105,0.00859477,0.089254,0.09051208,1.27212219,0.13598557,0.12880249,0.15886739,0.00607605,0.00606791,0.00862834,0.08932622,0.09142253,1.27183736,0.13664373,0.12987569,0.15959066,0.00596212,0.00585531,0.00872803,0.08916855,0.09210409,1.2725057,0.13681361,0.128125,0.15905253,0.00589193,0.0057312,0.00855103,0.09005051,0.09048665,1.27225342,0.136247,0.12744141,0.15904846,0.00625,0.0066213,0.00850627,0.08946253,0.09049377,1.27256266,0.13560003,0.12718608,0.15899658,0.00603943,0.00568339,0.00833842,0.08906377,0.09023336,1.26736145,0.13617681,0.12755127,0.15878601,0.00659993,0.00564677,0.00868835,0.08933843,0.09042257,1.26696777,0.13602524,0.12776896,0.15902303,0.0062561,0.00644124,0.0084849,0.08924179,0.09046834,1.26764425,0.13608933,0.12740987,0.15861104,0.00568441,0.00545247,0.00759684,0.08892136,0.09065755,1.26698201,0.13588893,0.12719421,0.15866598,0.00609131,0.00571594,0.00878398,0.0890831,0.09016215,1.26720276,0.13593267,0.12739156,0.15897013,0.00633036,0.00563761,0.00818278,0.08915634,0.0904541,1.26713562,0.13687973,0.12836609,0.15976257,0.00627645,0.00655619,0.00963745,0.08976771,0.09063212,1.26801147,0.12509613,0.12574158,0.16083069,0.00664876,0.0060791,0.00832011,0.08052928,0.08932699,1.26554464,0.12299042,0.12352804,0.15696615,0.00588786,0.00565694,0.00810343,0.07922821,0.08721008,1.24801941,0.12032522,0.11996969,0.14910482,0.00660807,0.00672607,0.00774028,0.07753652,0.08409932,1.22601522,0.12129364,0.12089132,0.14934692,0.00671488,0.00626119,0.00794474,0.07815603,0.08565877,1.22045186,0.12130686,0.12173971,0.15033264,0.0056783,0.00533651,0.00732015,0.07842255,0.0863149,1.22219442,0.12202606,0.12170003,0.15078328,0.00540873,0.00568848,0.00817973,0.07855988,0.08604533,1.22236226,0.12130483,0.1204071,0.15710017,0.00642395,0.00537008,0.00737305,0.07781932,0.0853831,1.20973994,0.08694052,0.07756755,0.09724019,0.00372416,0.00358988,0.00517883,0.0487353,0.05477905,0.75243327])
std = np.array([0.25528145,0.15723918,0.21439874,0.00644826,0.00522795,0.00657036,0.18630955,0.12297554,0.1940211,0.25528458,0.1566618,0.19759862,0.00533354,0.00791547,0.00664245,0.18630655,0.12484195,0.19378533,0.2554205,0.15696381,0.19752944,0.00525114,0.00548763,0.00677022,0.18628476,0.12318485,0.19394969,0.25549156,0.15689682,0.19776768,0.00600253,0.00533113,0.00644447,0.18630968,0.12303364,0.19403529,0.25536208,0.1563203,0.1976092,0.00527761,0.0053307,0.00671842,0.18625049,0.12297585,0.19388699,0.25568204,0.15806426,0.23766054,0.00624195,0.00537513,0.00675454,0.18665869,0.12338813,0.19940404,0.29904527,0.15652395,0.19955512,0.00539073,0.00555145,0.00680181,0.22450401,0.12285468,0.21923968,0.30517222,0.16373346,0.21071871,0.00601877,0.00622399,0.00651302,0.22454971,0.1250369,0.19385695,0.29906918,0.15671709,0.19813742,0.00571747,0.00529831,0.00605377,0.22356426,0.12318669,0.19398224,0.29925626,0.15676045,0.19781589,0.00561036,0.006132,0.00659849,0.22358217,0.12303779,0.19361318,0.29929695,0.15698547,0.19808379,0.00511842,0.00590558,0.00738868,0.22351966,0.12315185,0.19376212,0.29923096,0.15634272,0.1981905,0.00574496,0.0067573,0.00668421,0.22343415,0.12296588,0.19388133,0.29919467,0.15966503,0.19783063,0.00593314,0.00542002,0.00754083,0.22347715,0.12531236,0.19355353,0.29908236,0.15699866,0.19821635,0.00558058,0.00575429,0.00634581,0.22334447,0.12328711,0.19372682,0.29912444,0.15697536,0.19814524,0.00595985,0.00602595,0.00639735,0.22342628,0.12311754,0.193489,0.29937148,0.15711706,0.19814984,0.00531846,0.0051917,0.00638412,0.22349546,0.12334061,0.21020531,0.29915721,0.15687384,0.19824117,0.00619527,0.0055375,0.00697979,0.22344516,0.1231994,0.21035452,0.2992068,0.15684272,0.19810663,0.00562483,0.00643159,0.00659715,0.22344533,0.1232762,0.21001668,0.29918311,0.15705586,0.19835183,0.00542347,0.0055427,0.00598719,0.22350041,0.1231204,0.21037373,0.29925834,0.15711034,0.19830514,0.00592189,0.00524799,0.00632596,0.22345893,0.12339196,0.21025676,0.29924137,0.15698537,0.19810867,0.00609683,0.00518887,0.00593969,0.22343989,0.12313766,0.2102875,0.29901434,0.15687316,0.19943947,0.00593789,0.0060374,0.0067656,0.22331477,0.12308121,0.21212668,0.25542762,0.15624986,0.20582707,0.00596154,0.00583724,0.00621803,0.18638677,0.12222869,0.20955549,0.25496981,0.15462417,0.20472089,0.0058385,0.00539932,0.00667925,0.18632572,0.12111674,0.25059726,0.25471981,0.15391015,0.19428379,0.00869827,0.01017095,0.00746561,0.18646627,0.12078752,0.2954928,0.25498893,0.15478346,0.19433963,0.00840839,0.01015078,0.00624816,0.18646955,0.12101338,0.30597873,0.25500937,0.15479751,0.19538599,0.0060784,0.00549423,0.00590632,0.18630384,0.12113952,0.30668089,0.2548517,0.15451262,0.19555686,0.00530585,0.0057377,0.00651974,0.18633847,0.12095262,0.30665153,0.2556038,0.15628188,0.23576069,0.00606043,0.00513008,0.00615652,0.18703426,0.12151397,0.33843591,0.26515583,0.13723894,0.17409918,0.00555103,0.00537636,0.00677342,0.1406792,0.10556373,0.64569913])

def model(array):
	
	model = load_model('TEST271_kfold_model.h5')
	
	#test_row = np.array([0.046875,0.054931641,0.073486328,0.011230469,0.013671875,0.014160156,0.043457031,0.123046875,0.980957031])

	#파베꺼 = 매개변수로 넘어온 array 랑 똑같음

	#test_row = np.array([0.027099609,0.022949219,0.020507813,0.015380859,0.003173828,0.003173828,-0.015869141,0.030029297,1.120361328,0.028320313,0.027099609,0.032470703,0.010498047,0.007080078,0.008544922,-0.015869141,0.030273438,1.124755859,0.028808594,0.022216797,0.027099609,0.000732422,0.017089844,0.013183594,-0.016357422,0.030761719,1.122070313,0.029296875,0.030761719,0.038085938,0.006103516,0.005126953,0.004638672,-0.015869141,0.030761719,1.124511719,0.024902344,0.028076172,0.027099609,0.014160156,0.017333984,0.004638672,-0.016113281,0.030761719,1.123779297,0.028320313,0.029296875,0.034179688,0.005126953,0.001708984,0.0078125,-0.016357422,0.030029297,1.124755859,0.026855469,0.025390625,0.027099609,0.013671875,0.000732422,0.006591797,-0.015869141,0.029296875,1.124511719,0.028076172,0.028076172,0.026123047,0.016357422,0.004394531,0.017089844,-0.016113281,0.028320313,1.123291016,0.030029297,0.030273438,0.028564453,0.008056641,0.006103516,0.002441406,-0.015869141,0.031005859,1.124755859,0.029296875,0.025146484,0.030273438,0.006103516,0.006347656,0.004394531,-0.015869141,0.030761719,1.118896484,0.028564453,0.029541016,0.026855469,0.00390625,0.004150391,0.005615234,-0.016601563,0.031005859,1.124267578,0.058837891,0.141357422,0.070556641,0.014404297,0.005859375,0.007568359,0.046630859,0.158447266,1.133300781,0.026611328,0.027099609,0.025390625,0.010498047,0.009033203,0.008300781,0.059326172,0.045654297,1.123535156,0.019287109,0.018798828,0.031005859,0.010986328,0.014648438,0.000488281,0.062255859,0.046142578,1.124755859,0.029052734,0.018554688,0.021972656,0.000732422,0.000244141,0.009765625,0.062255859,0.046630859,1.124755859,0.028320313,0.025390625,0.109130859,0.009033203,0.014892578,0.000976563,0.062255859,0.046630859,1.214599609,0.024414063,0.019287109,0.028076172,0.003417969,0.004394531,0.003417969,0.062255859,0.045898438,1.124511719,0.023681641,0.021484375,0.030273438,0.005615234,0.00390625,0.008300781,0.061523438,0.046630859,1.124511719,0.028808594,0.026123047,0.037841797,0.009765625,0.010253906,0.000976563,0.061767578,0.046630859,1.124755859,0.030761719,0.01953125,0.028564453,0.008789063,0.004150391,0.003417969,0.062255859,0.044189453,1.124755859,0.029785156,0.013183594,0.028076172,0,0.004882813,0.004150391,0.061767578,0.046630859,1.123291016,0.028320313,0.023681641,0.025146484,0.014404297,0.006103516,0.018554688,0.062011719,0.046630859,1.124023438,0.029296875,0.023925781,0.026611328,0.008544922,0.003662109,0.004882813,0.061035156,0.046142578,1.124511719,0.025390625,0.017578125,0.037109375,0.002929688,0.001708984,0.017578125,0.060302734,0.046630859,1.124511719,0.045410156,0.053710938,0.102783203,0.010253906,0,0.006347656,0.062255859,0.058105469,1.205078125,0.027099609,0.026123047,0.024414063,0.010742188,0.000976563,0.005615234,0.062255859,0.060302734,1.121337891,0.022216797,0.038330078,0.033447266,0.001464844,0.004882813,0.014648438,0.059082031,0.056396484,1.124755859,0.02734375,0.031005859,0.024169922,0.011962891,0.000732422,0.007568359,0.062255859,0.046630859,1.122558594,0.026855469,0.034179688,0.039794922,0.018798828,0.007324219,0.002441406,0.061767578,0.046630859,1.137451172,0,0,0,0,0,0,0,0,0])
	test_row = array
	print(test_row.shape)
	test_row -= mean
	test_row /= std

	axisTen = model.predict(test_row.reshape(1,270))
	print('The Answer is ', axisTen) #정답: 9.735346

	return ''.join(str(e) for e in axisTen.tolist())
		

	#input10 model result(test할 input 9개값 넣기)
	#test_row = np.array([0.046875,0.054931641,0.073486328,0.011230469,0.013671875,0.014160156,0.043457031,0.123046875,0.980957031])
	#print('The Answer is ', model.predict(test_row.reshape(1,9)))
	# #정답: 10         #시험결과: 6.6102705
	# #정답: 3.543179   #시험결과: 6.3708177
	# #정답: 3.850158   #시험결과: 6.3968644
	# #정답: 6.320365   #시험결과: 6.695998