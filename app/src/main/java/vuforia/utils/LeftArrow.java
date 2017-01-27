/*===============================================================================
Copyright (c) 2016 PTC Inc. All Rights Reserved.

Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of PTC Inc., registered in the United States and other 
countries.
===============================================================================*/

package vuforia.utils;

import java.nio.Buffer;


public class LeftArrow extends MeshObject
{

    private Buffer mVertBuff;
    private Buffer mTexCoordBuff;
    private Buffer mNormBuff;

    private int verticesNumber = 0;


    public LeftArrow()
    {
        setVerts();
        setTexCoords();
        setNorms();
    }


    private void setVerts()
    {
        double[] TEAPOT_VERTS = {
                // f 1/1/1 3/2/1 4/3/1
                0.601726021258379, -0.1260266894594, -0.0264656246707593,
                0.601726021258379, 0.126026630103324, 0.0264655712502912,
                0.601726021258379, -0.1260266894594, 0.0264655712502912,
                // f 4/4/2 12/5/2 16/6/2
                0.601726021258379, -0.1260266894594, 0.0264655712502912,
                -0.0675303509873141, -0.119045643338245, 0.0249995948947839,
                -0.0675303509873141, -0.119045583982169, 2.96780378571296e-09,
                // f 6/7/3 10/8/3 15/9/3
                -0.0683913108640909, 0.276366547509345, -0.058036943221462,
                -0.0675303509873141, 0.119045583982169, -0.0249995889591764,
                -0.0675303509873141, 2.96780378373601e-08, -0.0249995889591764,
                // f 7/10/4 11/11/4 14/12/4
                -0.0683913108640909, 0.276366369441118, 0.0580370085131452,
                -0.0675303509873141, 0.119045583982169, 0.0249995948947839,
                -0.0675303509873141, 0.119045583982169, 2.96780378571296e-09,
                // f 8/13/5 12/14/5 13/15/5
                -0.0683913702201665, -0.276366547509345, 0.0580369491570695,
                -0.0675303509873141, -0.119045643338245, 0.0249995948947839,
                -0.0675303509873141, -2.96780377846413e-08, 0.0249995948947839,
                // f 5/16/6 20/17/6 17/18/6
                -0.0683913702201665, -0.276366428797194, -0.0580370025775376,
                -0.398273978741622, -0.00387693111282253, 0.000814190256996975,
                -0.398273978741622, -0.00157872322113547, -0.000572842517944077,
                // f 11/19/7 13/20/7 3/21/7
                -0.0675303509873141, 0.119045583982169, 0.0249995948947839,
                -0.0675303509873141, -2.96780377846413e-08, 0.0249995948947839,
                0.601726021258379, 0.126026630103324, 0.0264655712502912,
                // f 10/22/8 14/23/8 2/24/8
                -0.0675303509873141, 0.119045583982169, -0.0249995889591764,
                -0.0675303509873141, 0.119045583982169, 2.96780378571296e-09,
                0.601726021258379, 0.126026630103324, -0.0264656246707593,
                // f 9/25/9 15/26/9 1/27/9
                -0.0675303509873141, -0.119045524626094, -0.0249995889591764,
                -0.0675303509873141, 2.96780378373601e-08, -0.0249995889591764,
                0.601726021258379, -0.1260266894594, -0.0264656246707593,
                // f 16/28/10 12/14/10 8/13/10
                -0.0675303509873141, -0.119045583982169, 2.96780378571296e-09,
                -0.0675303509873141, -0.119045643338245, 0.0249995948947839,
                -0.0683913702201665, -0.276366547509345, 0.0580369491570695,
                // f 17/18/11 6/7/11 5/16/11
                -0.398273978741622, -0.00157872322113547, -0.000572842517944077,
                -0.0683913108640909, 0.276366547509345, -0.058036943221462,
                -0.0683913702201665, -0.276366428797194, -0.0580370025775376,
                // f 6/7/12 19/29/12 7/10/12
                -0.0683913108640909, 0.276366547509345, -0.058036943221462,
                -0.398273978741622, 0.00272791620109246, 0.000331566006181942,
                -0.0683913108640909, 0.276366369441118, 0.0580370085131452,
                // f 7/10/13 20/17/13 8/13/13
                -0.0683913108640909, 0.276366369441118, 0.0580370085131452,
                -0.398273978741622, -0.00387693111282253, 0.000814190256996975,
                -0.0683913702201665, -0.276366547509345, 0.0580369491570695,
                // f 1/1/1 2/30/1 3/2/1
                0.601726021258379, -0.1260266894594, -0.0264656246707593,
                0.601726021258379, 0.126026630103324, -0.0264656246707593,
                0.601726021258379, 0.126026630103324, 0.0264655712502912,
                // f 16/6/2 9/31/2 1/32/2
                -0.0675303509873141, -0.119045583982169, 2.96780378571296e-09,
                -0.0675303509873141, -0.119045524626094, -0.0249995889591764,
                0.601726021258379, -0.1260266894594, -0.0264656246707593,
                // f 1/32/2 4/4/2 16/6/2
                0.601726021258379, -0.1260266894594, -0.0264656246707593,
                0.601726021258379, -0.1260266894594, 0.0264655712502912,
                -0.0675303509873141, -0.119045583982169, 2.96780378571296e-09,
                // f 15/9/3 9/33/3 5/16/3
                -0.0675303509873141, 2.96780378373601e-08, -0.0249995889591764,
                -0.0675303509873141, -0.119045524626094, -0.0249995889591764,
                -0.0683913702201665, -0.276366428797194, -0.0580370025775376,
                // f 5/16/3 6/7/3 15/9/3
                -0.0683913702201665, -0.276366428797194, -0.0580370025775376,
                -0.0683913108640909, 0.276366547509345, -0.058036943221462,
                -0.0675303509873141, 2.96780378373601e-08, -0.0249995889591764,
                // f 14/12/4 10/8/4 6/7/4
                -0.0675303509873141, 0.119045583982169, 2.96780378571296e-09,
                -0.0675303509873141, 0.119045583982169, -0.0249995889591764,
                -0.0683913108640909, 0.276366547509345, -0.058036943221462,
                // f 6/7/4 7/10/4 14/12/4
                -0.0683913108640909, 0.276366547509345, -0.058036943221462,
                -0.0683913108640909, 0.276366369441118, 0.0580370085131452,
                -0.0675303509873141, 0.119045583982169, 2.96780378571296e-09,
                // f 13/15/5 11/11/5 7/10/5
                -0.0675303509873141, -2.96780377846413e-08, 0.0249995948947839,
                -0.0675303509873141, 0.119045583982169, 0.0249995948947839,
                -0.0683913108640909, 0.276366369441118, 0.0580370085131452,
                // f 7/10/5 8/13/5 13/15/5
                -0.0683913108640909, 0.276366369441118, 0.0580370085131452,
                -0.0683913702201665, -0.276366547509345, 0.0580369491570695,
                -0.0675303509873141, -2.96780377846413e-08, 0.0249995948947839,
                // f 5/16/14 8/13/14 20/17/14
                -0.0683913702201665, -0.276366428797194, -0.0580370025775376,
                -0.0683913702201665, -0.276366547509345, 0.0580369491570695,
                -0.398273978741622, -0.00387693111282253, 0.000814190256996975,
                // f 4/34/7 3/21/7 13/20/7
                0.601726021258379, -0.1260266894594, 0.0264655712502912,
                0.601726021258379, 0.126026630103324, 0.0264655712502912,
                -0.0675303509873141, -2.96780377846413e-08, 0.0249995948947839,
                // f 13/20/7 12/35/7 4/34/7
                -0.0675303509873141, -2.96780377846413e-08, 0.0249995948947839,
                -0.0675303509873141, -0.119045643338245, 0.0249995948947839,
                0.601726021258379, -0.1260266894594, 0.0264655712502912,
                // f 3/36/8 2/24/8 14/23/8
                0.601726021258379, 0.126026630103324, 0.0264655712502912,
                0.601726021258379, 0.126026630103324, -0.0264656246707593,
                -0.0675303509873141, 0.119045583982169, 2.96780378571296e-09,
                // f 14/23/8 11/37/8 3/36/8
                -0.0675303509873141, 0.119045583982169, 2.96780378571296e-09,
                -0.0675303509873141, 0.119045583982169, 0.0249995948947839,
                0.601726021258379, 0.126026630103324, 0.0264655712502912,
                // f 2/38/9 1/27/9 15/26/9
                0.601726021258379, 0.126026630103324, -0.0264656246707593,
                0.601726021258379, -0.1260266894594, -0.0264656246707593,
                -0.0675303509873141, 2.96780378373601e-08, -0.0249995889591764,
                // f 15/26/9 10/39/9 2/38/9
                -0.0675303509873141, 2.96780378373601e-08, -0.0249995889591764,
                -0.0675303509873141, 0.119045583982169, -0.0249995889591764,
                0.601726021258379, 0.126026630103324, -0.0264656246707593,
                // f 8/13/10 5/16/10 16/28/10
                -0.0683913702201665, -0.276366547509345, 0.0580369491570695,
                -0.0683913702201665, -0.276366428797194, -0.0580370025775376,
                -0.0675303509873141, -0.119045583982169, 2.96780378571296e-09,
                // f 9/33/10 16/28/10 5/16/10
                -0.0675303509873141, -0.119045524626094, -0.0249995889591764,
                -0.0675303509873141, -0.119045583982169, 2.96780378571296e-09,
                -0.0683913702201665, -0.276366428797194, -0.0580370025775376,
                // f 17/18/11 18/40/11 6/7/11
                -0.398273978741622, -0.00157872322113547, -0.000572842517944077,
                -0.398273978741622, 0.00272791620109246, -0.000572842517944077,
                -0.0683913108640909, 0.276366547509345, -0.058036943221462,
                // f 6/7/12 18/40/12 19/29/12
                -0.0683913108640909, 0.276366547509345, -0.058036943221462,
                -0.398273978741622, 0.00272791620109246, -0.000572842517944077,
                -0.398273978741622, 0.00272791620109246, 0.000331566006181942,
                // f 7/10/15 19/29/15 20/17/15
                -0.0683913108640909, 0.276366369441118, 0.0580370085131452,
                -0.398273978741622, 0.00272791620109246, 0.000331566006181942,
                -0.398273978741622, -0.00387693111282253, 0.000814190256996975,
        };
        mVertBuff = fillBuffer(TEAPOT_VERTS);
        verticesNumber = TEAPOT_VERTS.length / 3;
    }


    private void setTexCoords()
    {
        double[] TEAPOT_TEX_COORDS = {
                // f 1/1/1 3/2/1 4/3/1
                0.6343, 0.2498,
                0.5721, 0.4561,
                0.6343, 0.4561,
                // f 4/4/2 12/5/2 16/6/2
                1.0000, 1,
                0.9941, 0.4561,
                0.8931, 0.4561,
                // f 6/7/3 10/8/3 15/9/3
                0.8792, 0.8036,
                0.7680, 0.8371,
                0.7787, 0.8072,
                // f 7/10/4 11/11/4 14/12/4
                0.0001, 0.9999,
                0.2144, 0.8647,
                0.4922, 0.8647,
                // f 8/13/5 12/14/5 13/15/5
                0.1394, 0.8373,
                0.2410, 0.8013,
                0.2277, 0.833,
                // f 5/16/6 20/17/6 17/18/6
                0.9999, 0.6514,
                0.5039, 0.8028,
                0.5452, 0.9139,
                // f 11/19/7 13/20/7 3/21/7
                0.4340, 0,
                0.4047, 0,
                0.4358, 0.5477,
                // f 10/22/8 14/23/8 2/24/8
                0.5781, 1,
                0.6791, 1,
                0.5721, 0.4561,
                // f 9/25/9 15/26/9 1/27/9
                0.3718, 0,
                0.3425, 0,
                0.3736, 0.5477,
                // f 16/28/10 12/14/10 8/13/10
                0.5141, 0.7759,
                0.2410, 0.8013,
                0.1394, 0.8373,
                // f 17/18/11 6/7/11 5/16/11
                0.5452, 0.9139,
                0.8792, 0.8036,
                0.9999, 0.6514,
                // f 6/7/12 19/29/12 7/10/12
                0.8792, 0.8036,
                0.4572, 0.7181,
                0.0001, 0.9999,
                // f 7/10/13 20/17/13 8/13/13
                0.0001, 0.9999,
                0.5039, 0.8028,
                0.1394, 0.8373,
                // f 1/1/1 2/30/1 3/2/1
                0.6343, 0.2498,
                0.5721, 0.2498,
                0.5721, 0.4561,
                // f 16/6/2 9/31/2 1/32/2
                0.8931, 0.4561,
                0.7920, 0.4561,
                0.7861, 1,
                // f 1/32/2 4/4/2 16/6/2
                0.7861, 1,
                1.0000, 1,
                0.8931, 0.4561,
                // f 15/9/3 9/33/3 5/16/3
                0.7787, 0.8072,
                0.7895, 0.7773,
                0.9999, 0.6514,
                // f 5/16/3 6/7/3 15/9/3
                0.9999, 0.6514,
                0.8792, 0.8036,
                0.7787, 0.8072,
                // f 14/12/4 10/8/4 6/7/4
                0.4922, 0.8647,
                0.7680, 0.8371,
                0.8792, 0.8036,
                // f 6/7/4 7/10/4 14/12/4
                0.8792, 0.8036,
                0.0001, 0.9999,
                0.4922, 0.8647,
                // f 13/15/5 11/11/5 7/10/5
                0.2277, 0.833,
                0.2144, 0.8647,
                0.0001, 0.9999,
                // f 7/10/5 8/13/5 13/15/5
                0.0001, 0.9999,
                0.1394, 0.8373,
                0.2277, 0.833,
                // f 5/16/14 8/13/14 20/17/14
                0.9999, 0.6514,
                0.1394, 0.8373,
                0.5039, 0.8028,
                // f 4/34/7 3/21/7 13/20/7
                0.3736, 0.5477,
                0.4358, 0.5477,
                0.4047, 0,
                // f 13/20/7 12/35/7 4/34/7
                0.4047, 0,
                0.3753, 0,
                0.3736, 0.5477,
                // f 3/36/8 2/24/8 14/23/8
                0.7861, 0.4561,
                0.5721, 0.4561,
                0.6791, 1,
                // f 14/23/8 11/37/8 3/36/8
                0.6791, 1,
                0.7801, 1,
                0.7861, 0.4561,
                // f 2/38/9 1/27/9 15/26/9
                0.3114, 0.5477,
                0.3736, 0.5477,
                0.3425, 0,
                // f 15/26/9 10/39/9 2/38/9
                0.3425, 0,
                0.3131, 0,
                0.3114, 0.5477,
                // f 8/13/10 5/16/10 16/28/10
                0.1394, 0.8373,
                0.9999, 0.6514,
                0.5141, 0.7759,
                // f 9/33/10 16/28/10 5/16/10
                0.7895, 0.7773,
                0.5141, 0.7759,
                0.9999, 0.6514,
                // f 17/18/11 18/40/11 6/7/11
                0.5452, 0.9139,
                0.4974, 0.8508,
                0.8792, 0.8036,
                // f 6/7/12 18/40/12 19/29/12
                0.8792, 0.8036,
                0.4974, 0.8508,
                0.4572, 0.7181,
                // f 7/10/15 19/29/15 20/17/15
                0.0001, 0.9999,
                0.4572, 0.7181,
                0.5039, 0.8028,
        };
        mTexCoordBuff = fillBuffer(TEAPOT_TEX_COORDS);

    }


    private void setNorms()
    {
        double[] TEAPOT_NORMS = {
                // f 1/1/1 3/2/1 4/3/1
                1, 0, 0,
                1, 0, 0,
                1, 0, 0,
                // f 4/4/2 12/5/2 16/6/2
                -0.0104004775488902, -0.999945913570707, 0,
                -0.0104004775488902, -0.999945913570707, 0,
                -0.0104004775488902, -0.999945913570707, 0,
                // f 6/7/3 10/8/3 15/9/3
                0.999659364672722, 0, -0.026098939099688,
                0.999659364672722, 0, -0.026098939099688,
                0.999659364672722, 0, -0.026098939099688,
                // f 7/10/4 11/11/4 14/12/4
                0.99998487534314, 0.00549991681438727, 0,
                0.99998487534314, 0.00549991681438727, 0,
                0.99998487534314, 0.00549991681438727, 0,
                // f 8/13/5 12/14/5 13/15/5
                0.999659364672722, 0, 0.026098939099688,
                0.999659364672722, 0, 0.026098939099688,
                0.999659364672722, 0, 0.026098939099688,
                // f 5/16/6 20/17/6 17/18/6
                -0.501415829947631, -0.447114115615448, -0.740723385006403,
                -0.501415829947631, -0.447114115615448, -0.740723385006403,
                -0.501415829947631, -0.447114115615448, -0.740723385006403,
                // f 11/19/7 13/20/7 3/21/7
                -0.00219999467601933, 0, 0.999997580008785,
                -0.00219999467601933, 0, 0.999997580008785,
                -0.00219999467601933, 0, 0.999997580008785,
                // f 10/22/8 14/23/8 2/24/8
                -0.0104004775488902, 0.999945913570707, 0,
                -0.0104004775488902, 0.999945913570707, 0,
                -0.0104004775488902, 0.999945913570707, 0,
                // f 9/25/9 15/26/9 1/27/9
                -0.00219999467601933, 0, -0.999997580008785,
                -0.00219999467601933, 0, -0.999997580008785,
                -0.00219999467601933, 0, -0.999997580008785,
                // f 16/28/10 12/14/10 8/13/10
                0.99998487534314, -0.00549991681438727, 0,
                0.99998487534314, -0.00549991681438727, 0,
                0.99998487534314, -0.00549991681438727, 0,
                // f 17/18/11 6/7/11 5/16/11
                -0.171594371796906, 0, -0.985167687029789,
                -0.171594371796906, 0, -0.985167687029789,
                -0.171594371796906, 0, -0.985167687029789,
                // f 6/7/12 19/29/12 7/10/12
                -0.638402346132933, 0.769702828663093, 0,
                -0.638402346132933, 0.769702828663093, 0,
                -0.638402346132933, 0.769702828663093, 0,
                // f 7/10/13 20/17/13 8/13/13
                -0.170898043228607, 0, 0.985288718508759,
                -0.170898043228607, 0, 0.985288718508759,
                -0.170898043228607, 0, 0.985288718508759,
                // f 1/1/1 2/30/1 3/2/1
                1, 0, 0,
                1, 0, 0,
                1, 0, 0,
                // f 16/6/2 9/31/2 1/32/2
                -0.0104004775488902, -0.999945913570707, 0,
                -0.0104004775488902, -0.999945913570707, 0,
                -0.0104004775488902, -0.999945913570707, 0,
                // f 1/32/2 4/4/2 16/6/2
                -0.0104004775488902, -0.999945913570707, 0,
                -0.0104004775488902, -0.999945913570707, 0,
                -0.0104004775488902, -0.999945913570707, 0,
                // f 15/9/3 9/33/3 5/16/3
                0.999659364672722, 0, -0.026098939099688,
                0.999659364672722, 0, -0.026098939099688,
                0.999659364672722, 0, -0.026098939099688,
                // f 5/16/3 6/7/3 15/9/3
                0.999659364672722, 0, -0.026098939099688,
                0.999659364672722, 0, -0.026098939099688,
                0.999659364672722, 0, -0.026098939099688,
                // f 14/12/4 10/8/4 6/7/4
                0.99998487534314, 0.00549991681438727, 0,
                0.99998487534314, 0.00549991681438727, 0,
                0.99998487534314, 0.00549991681438727, 0,
                // f 6/7/4 7/10/4 14/12/4
                0.99998487534314, 0.00549991681438727, 0,
                0.99998487534314, 0.00549991681438727, 0,
                0.99998487534314, 0.00549991681438727, 0,
                // f 13/15/5 11/11/5 7/10/5
                0.999659364672722, 0, 0.026098939099688,
                0.999659364672722, 0, 0.026098939099688,
                0.999659364672722, 0, 0.026098939099688,
                // f 7/10/5 8/13/5 13/15/5
                0.999659364672722, 0, 0.026098939099688,
                0.999659364672722, 0, 0.026098939099688,
                0.999659364672722, 0, 0.026098939099688,
                // f 5/16/14 8/13/14 20/17/14
                -0.636873694475313, -0.770968155817972, 0,
                -0.636873694475313, -0.770968155817972, 0,
                -0.636873694475313, -0.770968155817972, 0,
                // f 4/34/7 3/21/7 13/20/7
                -0.00219999467601933, 0, 0.999997580008785,
                -0.00219999467601933, 0, 0.999997580008785,
                -0.00219999467601933, 0, 0.999997580008785,
                // f 13/20/7 12/35/7 4/34/7
                -0.00219999467601933, 0, 0.999997580008785,
                -0.00219999467601933, 0, 0.999997580008785,
                -0.00219999467601933, 0, 0.999997580008785,
                // f 3/36/8 2/24/8 14/23/8
                -0.0104004775488902, 0.999945913570707, 0,
                -0.0104004775488902, 0.999945913570707, 0,
                -0.0104004775488902, 0.999945913570707, 0,
                // f 14/23/8 11/37/8 3/36/8
                -0.0104004775488902, 0.999945913570707, 0,
                -0.0104004775488902, 0.999945913570707, 0,
                -0.0104004775488902, 0.999945913570707, 0,
                // f 2/38/9 1/27/9 15/26/9
                -0.00219999467601933, 0, -0.999997580008785,
                -0.00219999467601933, 0, -0.999997580008785,
                -0.00219999467601933, 0, -0.999997580008785,
                // f 15/26/9 10/39/9 2/38/9
                -0.00219999467601933, 0, -0.999997580008785,
                -0.00219999467601933, 0, -0.999997580008785,
                -0.00219999467601933, 0, -0.999997580008785,
                // f 8/13/10 5/16/10 16/28/10
                0.99998487534314, -0.00549991681438727, 0,
                0.99998487534314, -0.00549991681438727, 0,
                0.99998487534314, -0.00549991681438727, 0,
                // f 9/33/10 16/28/10 5/16/10
                0.99998487534314, -0.00549991681438727, 0,
                0.99998487534314, -0.00549991681438727, 0,
                0.99998487534314, -0.00549991681438727, 0,
                // f 17/18/11 18/40/11 6/7/11
                -0.171594371796906, 0, -0.985167687029789,
                -0.171594371796906, 0, -0.985167687029789,
                -0.171594371796906, 0, -0.985167687029789,
                // f 6/7/12 18/40/12 19/29/12
                -0.638402346132933, 0.769702828663093, 0,
                -0.638402346132933, 0.769702828663093, 0,
                -0.638402346132933, 0.769702828663093, 0,
                // f 7/10/15 19/29/15 20/17/15
                -0.228702594645654, 0.0709008043741885, 0.970911015047949,
                -0.228702594645654, 0.0709008043741885, 0.970911015047949,
                -0.228702594645654, 0.0709008043741885, 0.970911015047949,
        };
        mNormBuff = fillBuffer(TEAPOT_NORMS);
    }


    public int getNumObjectIndex()
    {
        return 0;
    }


    @Override
    public int getNumObjectVertex()
    {
        return verticesNumber;
    }


    @Override
    public Buffer getBuffer(BUFFER_TYPE bufferType)
    {
        Buffer result = null;
        switch (bufferType)
        {
            case BUFFER_TYPE_VERTEX:
                result = mVertBuff;
                break;
            case BUFFER_TYPE_TEXTURE_COORD:
                result = mTexCoordBuff;
                break;
            case BUFFER_TYPE_NORMALS:
                result = mNormBuff;
                break;
            default:
                break;

        }

        return result;
    }

}