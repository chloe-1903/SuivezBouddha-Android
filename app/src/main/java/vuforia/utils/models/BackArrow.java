/*===============================================================================
Copyright (c) 2016 PTC Inc. All Rights Reserved.

Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of PTC Inc., registered in the United States and other 
countries.
===============================================================================*/

package vuforia.utils.models;

import java.nio.Buffer;

import vuforia.utils.MeshObject;


public class BackArrow extends MeshObject
{

    private Buffer mVertBuff;
    private Buffer mTexCoordBuff;
    private Buffer mNormBuff;

    private int verticesNumber = 0;


    public BackArrow()
    {
        setVerts();
        setTexCoords();
        setNorms();
    }


    private void setVerts()
    {
        double[] TEAPOT_VERTS = {
                // f 1/1/1 3/2/1 4/3/1
                -0.137323604746832, 0.303231840894226, -0.582046336603669,
                0.137323643552866, 0.250959660286804, -0.606421247390213,
                -0.137323669423555, 0.250959660286804, -0.606421247390213,
                // f 4/4/2 12/5/2 16/6/2
                -0.137323669423555, 0.250959660286804, -0.606421247390213,
                -0.129716845951622, -0.0557861284338732, 0.0551772003166831,
                -0.129716781274899, -0.0310977942456214, 0.0666895277325059,
                // f 6/7/3 10/8/3 15/9/3
                0.301139953121017, 0.0258199860518178, 0.0942658716517588,
                0.129716755404209, -0.00640958941081643, 0.0782018551483286,
                1.94030170287632e-08, -0.006409524734093, 0.0782018551483286,
                // f 7/10/4 11/11/4 14/12/4
                0.301139759090847, -0.0888087052023952, 0.0408136642254868,
                0.129716690727486, -0.0557861931105967, 0.0551771356399597,
                0.129716755404209, -0.0310978589223448, 0.0666895277325059,
                // f 8/13/5 12/14/5 13/15/5
                -0.301139914314983, -0.0888085758489483, 0.0408136642254868,
                -0.129716845951622, -0.0557861284338732, 0.0551772003166831,
                -4.52737063941644e-08, -0.0557861284338732, 0.0551772003166831,
                // f 5/16/6 20/17/6 17/18/6
                -0.301139784961536, 0.0258201154052646, 0.0942659363284822,
                -0.00422447014079411, -0.184209782703672, 0.392940069965986,
                -0.00172025208658428, -0.182840059055023, 0.393578752609787,
                // f 11/19/7 13/20/7 3/21/7
                0.129716690727486, -0.0557861931105967, 0.0551771356399597,
                -4.52737063941644e-08, -0.0557861284338732, 0.0551772003166831,
                0.137323643552866, 0.250959660286804, -0.606421247390213,
                // f 10/22/8 14/23/8 2/24/8
                0.129716755404209, -0.00640958941081643, 0.0782018551483286,
                0.129716755404209, -0.0310978589223448, 0.0666895277325059,
                0.137323643552866, 0.303231840894226, -0.582046336603669,
                // f 9/25/9 15/26/9 1/27/9
                -0.129716716598175, -0.006409524734093, 0.0782018551483286,
                1.94030170287632e-08, -0.006409524734093, 0.0782018551483286,
                -0.137323604746832, 0.303231840894226, -0.582046336603669,
                // f 16/28/10 12/14/10 8/13/10
                -0.129716781274899, -0.0310977942456214, 0.0666895277325059,
                -0.129716845951622, -0.0557861284338732, 0.0551772003166831,
                -0.301139914314983, -0.0888085758489483, 0.0408136642254868,
                // f 17/18/11 6/7/11 5/16/11
                -0.00172025208658428, -0.182840059055023, 0.393578752609787,
                0.301139953121017, 0.0258199860518178, 0.0942658716517588,
                -0.301139784961536, 0.0258201154052646, 0.0942659363284822,
                // f 6/7/12 19/29/12 7/10/12
                0.301139953121017, 0.0258199860518178, 0.0942658716517588,
                0.00297243225808496, -0.183733179928769, 0.393162299187667,
                0.301139759090847, -0.0888087052023952, 0.0408136642254868,
                // f 7/10/13 20/17/13 8/13/13
                0.301139759090847, -0.0888087052023952, 0.0408136642254868,
                -0.00422447014079411, -0.184209782703672, 0.392940069965986,
                -0.301139914314983, -0.0888085758489483, 0.0408136642254868,
                // f 1/1/1 2/30/1 3/2/1
                -0.137323604746832, 0.303231840894226, -0.582046336603669,
                0.137323643552866, 0.303231840894226, -0.582046336603669,
                0.137323643552866, 0.250959660286804, -0.606421247390213,
                // f 16/6/2 9/31/2 1/32/2
                -0.129716781274899, -0.0310977942456214, 0.0666895277325059,
                -0.129716716598175, -0.006409524734093, 0.0782018551483286,
                -0.137323604746832, 0.303231840894226, -0.582046336603669,
                // f 1/32/2 4/4/2 16/6/2
                -0.137323604746832, 0.303231840894226, -0.582046336603669,
                -0.137323669423555, 0.250959660286804, -0.606421247390213,
                -0.129716781274899, -0.0310977942456214, 0.0666895277325059,
                // f 15/9/3 9/33/3 5/16/3
                1.94030170287632e-08, -0.006409524734093, 0.0782018551483286,
                -0.129716716598175, -0.006409524734093, 0.0782018551483286,
                -0.301139784961536, 0.0258201154052646, 0.0942659363284822,
                // f 5/16/3 6/7/3 15/9/3
                -0.301139784961536, 0.0258201154052646, 0.0942659363284822,
                0.301139953121017, 0.0258199860518178, 0.0942658716517588,
                1.94030170287632e-08, -0.006409524734093, 0.0782018551483286,
                // f 14/12/4 10/8/4 6/7/4
                0.129716755404209, -0.0310978589223448, 0.0666895277325059,
                0.129716755404209, -0.00640958941081643, 0.0782018551483286,
                0.301139953121017, 0.0258199860518178, 0.0942658716517588,
                // f 6/7/4 7/10/4 14/12/4
                0.301139953121017, 0.0258199860518178, 0.0942658716517588,
                0.301139759090847, -0.0888087052023952, 0.0408136642254868,
                0.129716755404209, -0.0310978589223448, 0.0666895277325059,
                // f 13/15/5 11/11/5 7/10/5
                -4.52737063941644e-08, -0.0557861284338732, 0.0551772003166831,
                0.129716690727486, -0.0557861931105967, 0.0551771356399597,
                0.301139759090847, -0.0888087052023952, 0.0408136642254868,
                // f 7/10/5 8/13/5 13/15/5
                0.301139759090847, -0.0888087052023952, 0.0408136642254868,
                -0.301139914314983, -0.0888085758489483, 0.0408136642254868,
                -4.52737063941644e-08, -0.0557861284338732, 0.0551772003166831,
                // f 5/16/14 8/13/14 20/17/14
                -0.301139784961536, 0.0258201154052646, 0.0942659363284822,
                -0.301139914314983, -0.0888085758489483, 0.0408136642254868,
                -0.00422447014079411, -0.184209782703672, 0.392940069965986,
                // f 4/34/7 3/21/7 13/20/7
                -0.137323669423555, 0.250959660286804, -0.606421247390213,
                0.137323643552866, 0.250959660286804, -0.606421247390213,
                -4.52737063941644e-08, -0.0557861284338732, 0.0551772003166831,
                // f 13/20/7 12/35/7 4/34/7
                -4.52737063941644e-08, -0.0557861284338732, 0.0551772003166831,
                -0.129716845951622, -0.0557861284338732, 0.0551772003166831,
                -0.137323669423555, 0.250959660286804, -0.606421247390213,
                // f 3/36/8 2/24/8 14/23/8
                0.137323643552866, 0.250959660286804, -0.606421247390213,
                0.137323643552866, 0.303231840894226, -0.582046336603669,
                0.129716755404209, -0.0310978589223448, 0.0666895277325059,
                // f 14/23/8 11/37/8 3/36/8
                0.129716755404209, -0.0310978589223448, 0.0666895277325059,
                0.129716690727486, -0.0557861931105967, 0.0551771356399597,
                0.137323643552866, 0.250959660286804, -0.606421247390213,
                // f 2/38/9 1/27/9 15/26/9
                0.137323643552866, 0.303231840894226, -0.582046336603669,
                -0.137323604746832, 0.303231840894226, -0.582046336603669,
                1.94030170287632e-08, -0.006409524734093, 0.0782018551483286,
                // f 15/26/9 10/39/9 2/38/9
                1.94030170287632e-08, -0.006409524734093, 0.0782018551483286,
                0.129716755404209, -0.00640958941081643, 0.0782018551483286,
                0.137323643552866, 0.303231840894226, -0.582046336603669,
                // f 8/13/10 5/16/10 16/28/10
                -0.301139914314983, -0.0888085758489483, 0.0408136642254868,
                -0.301139784961536, 0.0258201154052646, 0.0942659363284822,
                -0.129716781274899, -0.0310977942456214, 0.0666895277325059,
                // f 9/33/10 16/28/10 5/16/10
                -0.129716716598175, -0.006409524734093, 0.0782018551483286,
                -0.129716781274899, -0.0310977942456214, 0.0666895277325059,
                -0.301139784961536, 0.0258201154052646, 0.0942659363284822,
                // f 17/18/11 18/40/11 6/7/11
                -0.00172025208658428, -0.182840059055023, 0.393578752609787,
                0.00297243225808496, -0.182840059055023, 0.393578752609787,
                0.301139953121017, 0.0258199860518178, 0.0942658716517588,
                // f 6/7/12 18/40/12 19/29/12
                0.301139953121017, 0.0258199860518178, 0.0942658716517588,
                0.00297243225808496, -0.182840059055023, 0.393578752609787,
                0.00297243225808496, -0.183733179928769, 0.393162299187667,
                // f 7/10/15 19/29/15 20/17/15
                0.301139759090847, -0.0888087052023952, 0.0408136642254868,
                0.00297243225808496, -0.183733179928769, 0.393162299187667,
                -0.00422447014079411, -0.184209782703672, 0.392940069965986,
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
                0, 0.422606244053384, -0.906313390879276,
                0, 0.422606244053384, -0.906313390879276,
                0, 0.422606244053384, -0.906313390879276,
                // f 4/4/2 12/5/2 16/6/2
                -0.999945188544129, -0.00440019884947911, 0.00950042933410263,
                -0.999945188544129, -0.00440019884947911, 0.00950042933410263,
                -0.999945188544129, -0.00440019884947911, 0.00950042933410263,
                // f 6/7/3 10/8/3 15/9/3
                0, 0.44609326181217, -0.894986481331299,
                0, 0.44609326181217, -0.894986481331299,
                0, 0.44609326181217, -0.894986481331299,
                // f 7/10/4 11/11/4 14/12/4
                0.00549999807500101, 0.422599852090078, -0.906299682795166,
                0.00549999807500101, 0.422599852090078, -0.906299682795166,
                0.00549999807500101, 0.422599852090078, -0.906299682795166,
                // f 8/13/5 12/14/5 13/15/5
                0, 0.398897963631093, -0.916995318750847,
                0, 0.398897963631093, -0.916995318750847,
                0, 0.398897963631093, -0.916995318750847,
                // f 5/16/6 20/17/6 17/18/6
                -0.447099324880529, 0.459399306307571, 0.767498841077625,
                -0.447099324880529, 0.459399306307571, 0.767498841077625,
                -0.447099324880529, 0.459399306307571, 0.767498841077625,
                // f 11/19/7 13/20/7 3/21/7
                0, -0.907238014069201, -0.420617624247692,
                0, -0.907238014069201, -0.420617624247692,
                0, -0.907238014069201, -0.420617624247692,
                // f 10/22/8 14/23/8 2/24/8
                0.999945188544129, -0.00440019884947911, 0.00950042933410263,
                0.999945188544129, -0.00440019884947911, 0.00950042933410263,
                0.999945188544129, -0.00440019884947911, 0.00950042933410263,
                // f 9/25/9 15/26/9 1/27/9
                0, 0.905384463735902, 0.42459271405154,
                0, 0.905384463735902, 0.42459271405154,
                0, 0.905384463735902, 0.42459271405154,
                // f 16/28/10 12/14/10 8/13/10
                -0.00549999807500101, 0.422599852090078, -0.906299682795166,
                -0.00549999807500101, 0.422599852090078, -0.906299682795166,
                -0.00549999807500101, 0.422599852090078, -0.906299682795166,
                // f 17/18/11 6/7/11 5/16/11
                0, 0.820315709196248, 0.571910952199603,
                0, 0.820315709196248, 0.571910952199603,
                0, 0.820315709196248, 0.571910952199603,
                // f 6/7/12 19/29/12 7/10/12
                0.769696886582391, -0.269798908665622, 0.5785976595772,
                0.769696886582391, -0.269798908665622, 0.5785976595772,
                0.769696886582391, -0.269798908665622, 0.5785976595772,
                // f 7/10/13 20/17/13 8/13/13
                0, -0.965203238262297, -0.261500877336915,
                0, -0.965203238262297, -0.261500877336915,
                0, -0.965203238262297, -0.261500877336915,
                // f 1/1/1 2/30/1 3/2/1
                0, 0.422606244053384, -0.906313390879276,
                0, 0.422606244053384, -0.906313390879276,
                0, 0.422606244053384, -0.906313390879276,
                // f 16/6/2 9/31/2 1/32/2
                -0.999945188544129, -0.00440019884947911, 0.00950042933410263,
                -0.999945188544129, -0.00440019884947911, 0.00950042933410263,
                -0.999945188544129, -0.00440019884947911, 0.00950042933410263,
                // f 1/32/2 4/4/2 16/6/2
                -0.999945188544129, -0.00440019884947911, 0.00950042933410263,
                -0.999945188544129, -0.00440019884947911, 0.00950042933410263,
                -0.999945188544129, -0.00440019884947911, 0.00950042933410263,
                // f 15/9/3 9/33/3 5/16/3
                0, 0.44609326181217, -0.894986481331299,
                0, 0.44609326181217, -0.894986481331299,
                0, 0.44609326181217, -0.894986481331299,
                // f 5/16/3 6/7/3 15/9/3
                0, 0.44609326181217, -0.894986481331299,
                0, 0.44609326181217, -0.894986481331299,
                0, 0.44609326181217, -0.894986481331299,
                // f 14/12/4 10/8/4 6/7/4
                0.00549999807500101, 0.422599852090078, -0.906299682795166,
                0.00549999807500101, 0.422599852090078, -0.906299682795166,
                0.00549999807500101, 0.422599852090078, -0.906299682795166,
                // f 6/7/4 7/10/4 14/12/4
                0.00549999807500101, 0.422599852090078, -0.906299682795166,
                0.00549999807500101, 0.422599852090078, -0.906299682795166,
                0.00549999807500101, 0.422599852090078, -0.906299682795166,
                // f 13/15/5 11/11/5 7/10/5
                0, 0.398897963631093, -0.916995318750847,
                0, 0.398897963631093, -0.916995318750847,
                0, 0.398897963631093, -0.916995318750847,
                // f 7/10/5 8/13/5 13/15/5
                0, 0.398897963631093, -0.916995318750847,
                0, 0.398897963631093, -0.916995318750847,
                0, 0.398897963631093, -0.916995318750847,
                // f 5/16/14 8/13/14 20/17/14
                -0.770993966995812, -0.269097894317215, 0.577195483463013,
                -0.770993966995812, -0.269097894317215, 0.577195483463013,
                -0.770993966995812, -0.269097894317215, 0.577195483463013,
                // f 4/34/7 3/21/7 13/20/7
                0, -0.907238014069201, -0.420617624247692,
                0, -0.907238014069201, -0.420617624247692,
                0, -0.907238014069201, -0.420617624247692,
                // f 13/20/7 12/35/7 4/34/7
                0, -0.907238014069201, -0.420617624247692,
                0, -0.907238014069201, -0.420617624247692,
                0, -0.907238014069201, -0.420617624247692,
                // f 3/36/8 2/24/8 14/23/8
                0.999945188544129, -0.00440019884947911, 0.00950042933410263,
                0.999945188544129, -0.00440019884947911, 0.00950042933410263,
                0.999945188544129, -0.00440019884947911, 0.00950042933410263,
                // f 14/23/8 11/37/8 3/36/8
                0.999945188544129, -0.00440019884947911, 0.00950042933410263,
                0.999945188544129, -0.00440019884947911, 0.00950042933410263,
                0.999945188544129, -0.00440019884947911, 0.00950042933410263,
                // f 2/38/9 1/27/9 15/26/9
                0, 0.905384463735902, 0.42459271405154,
                0, 0.905384463735902, 0.42459271405154,
                0, 0.905384463735902, 0.42459271405154,
                // f 15/26/9 10/39/9 2/38/9
                0, 0.905384463735902, 0.42459271405154,
                0, 0.905384463735902, 0.42459271405154,
                0, 0.905384463735902, 0.42459271405154,
                // f 8/13/10 5/16/10 16/28/10
                -0.00549999807500101, 0.422599852090078, -0.906299682795166,
                -0.00549999807500101, 0.422599852090078, -0.906299682795166,
                -0.00549999807500101, 0.422599852090078, -0.906299682795166,
                // f 9/33/10 16/28/10 5/16/10
                -0.00549999807500101, 0.422599852090078, -0.906299682795166,
                -0.00549999807500101, 0.422599852090078, -0.906299682795166,
                -0.00549999807500101, 0.422599852090078, -0.906299682795166,
                // f 17/18/11 18/40/11 6/7/11
                0, 0.820315709196248, 0.571910952199603,
                0, 0.820315709196248, 0.571910952199603,
                0, 0.820315709196248, 0.571910952199603,
                // f 6/7/12 18/40/12 19/29/12
                0.769696886582391, -0.269798908665622, 0.5785976595772,
                0.769696886582391, -0.269798908665622, 0.5785976595772,
                0.769696886582391, -0.269798908665622, 0.5785976595772,
                // f 7/10/15 19/29/15 20/17/15
                0.0708991499242886, -0.97658829077659, -0.203097564874796,
                0.0708991499242886, -0.97658829077659, -0.203097564874796,
                0.0708991499242886, -0.97658829077659, -0.203097564874796,
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