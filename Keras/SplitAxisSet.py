#load python libraries
from __future__ import print_function
import numpy as np
import pandas as pd

#load data from csv file and define the label column
axis_df = pd.read_csv("./axis_df.csv")

#col num : 271(input 270 + label 1)
axis_data = pd.DataFrame(axis_df, columns=['m1_x','m1_y','m1_z','d1_x','d1_y','d1_z','r1_x','r1_y','r1_z',
                                           'm2_x','m2_y','m2_z','d2_x','d2_y','d2_z','r2_x','r2_y','r2_z',
                                           'm3_x','m3_y','m3_z','d3_x','d3_y','d3_z','r3_x','r3_y','r3_z',
                                           'm4_x','m4_y','m4_z','d4_x','d4_y','d4_z','r4_x','r4_y','r4_z',
                                           'm5_x','m5_y','m5_z','d5_x','d5_y','d5_z','r5_x','r5_y','r5_z',
                                           'm6_x','m6_y','m6_z','d6_x','d6_y','d6_z','r6_x','r6_y','r6_z',
                                           'm7_x','m7_y','m7_z','d7_x','d7_y','d7_z','r7_x','r7_y','r7_z',
                                           'm8_x','m8_y','m8_z','d8_x','d8_y','d8_z','r8_x','r8_y','r8_z',
                                           'm9_x','m9_y','m9_z','d9_x','d9_y','d9_z','r9_x','r9_y','r9_z',
                                           'm10_x','m10_y','m10_z','d10_x','d10_y','d10_z','r10_x','r10_y','r10_z',
                                           'm11_x','m11_y','m11_z','d11_x','d11_y','d11_z','r11_x','r11_y','r11_z',
                                           'm12_x','m12_y','m12_z','d12_x','d12_y','d12_z','r12_x','r12_y','r12_z',
                                           'm13_x','m13_y','m13_z','d13_x','d13_y','d13_z','r13_x','r13_y','r13_z',
                                           'm14_x','m14_y','m14_z','d14_x','d14_y','d14_z','r14_x','r14_y','r14_z',
                                           'm15_x','m15_y','m15_z','d15_x','d15_y','d15_z','r15_x','r15_y','r15_z',
                                           'm16_x','m16_y','m16_z','d16_x','d16_y','d16_z','r16_x','r16_y','r16_z',
                                           'm17_x','m17_y','m17_z','d17_x','d17_y','d17_z','r17_x','r17_y','r17_z',
                                           'm18_x','m18_y','m18_z','d18_x','d18_y','d18_z','r18_x','r18_y','r18_z',
                                           'm19_x','m19_y','m19_z','d19_x','d19_y','d19_z','r19_x','r19_y','r19_z',
                                           'm20_x','m20_y','m20_z','d20_x','d20_y','d20_z','r20_x','r20_y','r20_z',
                                           'm21_x','m21_y','m21_z','d21_x','d21_y','d21_z','r21_x','r21_y','r21_z',
                                           'm22_x','m22_y','m22_z','d22_x','d22_y','d22_z','r22_x','r22_y','r22_z',
                                           'm23_x','m23_y','m23_z','d23_x','d23_y','d23_z','r23_x','r23_y','r23_z',
                                           'm24_x','m24_y','m24_z','d24_x','d24_y','d24_z','r24_x','r24_y','r24_z',
                                           'm25_x','m25_y','m25_z','d25_x','d25_y','d25_z','r25_x','r25_y','r25_z',
                                           'm26_x','m26_y','m26_z','d26_x','d26_y','d26_z','r26_x','r26_y','r26_z',
                                           'm27_x','m27_y','m27_z','d27_x','d27_y','d27_z','r27_x','r27_y','r27_z',
                                           'm28_x','m28_y','m28_z','d28_x','d28_y','d28_z','r28_x','r28_y','r28_z',
                                           'm29_x','m29_y','m29_z','d29_x','d29_y','d29_z','r29_x','r29_y','r29_z',
                                           'm30_x','m30_y','m30_z','d30_x','d30_y','d30_z','r30_x','r30_y','r30_z',
                                           'label'])
label_col = 'label'
# print(axis_data.describe())

#split data for training, validation, test
def train_validate_test_split(df, train_part=.6, validate_part=.2, test_part=.2, seed=None):
    np.random.seed(seed)
    total_size = train_part + validate_part + test_part
    train_percent = train_part / total_size
    validate_percent = validate_part / total_size
    test_percent = test_part / total_size
    perm = np.random.permutation(df.index) #shuffle permutation
    m = len(df)
    train_end = int(train_percent * m)
    validate_end = int(validate_percent * m) + train_end
    train = perm[:train_end]
    validate = perm[train_end:validate_end]
    test = perm[validate_end:]

    return train, validate, test

train_size, valid_size, test_size = (70, 15, 15)
ax_train, ax_valid, ax_test = train_validate_test_split(axis_data,
                                                              train_part=train_size,
                                                              validate_part=valid_size,
                                                              test_part=test_size,
                                                              seed=1234)

#each dataset has unique number
# print(ax_train)
# print(ax_valid)
# print(ax_test)

#export to csv file
ax_xy_train = axis_data.loc[ax_train, :]
ax_xy_valid = axis_data.loc[ax_valid, :]
ax_xy_test = axis_data.loc[ax_test, :]
ax_xy_train.to_csv("./ax_train.csv", mode='w')
ax_xy_valid.to_csv("./ax_valid.csv", mode='w') if valid_size != 0 else print("")
ax_xy_test.to_csv("./ax_test.csv", mode='w')

print("[INFO] Split dataset complete")
print('Size of training set: ', len(ax_xy_train))
print('Size of validation set: ', len(ax_xy_valid))
print('Size of test set: ', len(ax_test), '(not converted)')


#separate x and y
ax_x_train = axis_data.loc[ax_train, :].drop(label_col, axis=1)
ax_y_train = axis_data.loc[ax_train, [label_col]]
ax_x_valid = axis_data.loc[ax_valid, :].drop(label_col, axis=1)
ax_y_valid = axis_data.loc[ax_valid, [label_col]]
ax_x_test = axis_data.loc[ax_test, :].drop(label_col, axis=1)
ax_y_test = axis_data.loc[ax_test, [label_col]]

# print("------- train_x -------\n", ax_x_train)
# print("------- train_y -------\n", ax_y_train)
# print("------- valid_x -------\n", ax_x_valid)
# print("------- valid_y -------\n", ax_y_valid)
# print("------- test_x -------\n", ax_x_test)
# print("------- test_y -------\n", ax_y_test)
