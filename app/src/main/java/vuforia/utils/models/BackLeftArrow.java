/*===============================================================================
Copyright (c) 2016 PTC Inc. All Rights Reserved.

Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of PTC Inc., registered in the United States and other 
countries.
===============================================================================*/

package vuforia.utils.models;

import java.nio.Buffer;

import vuforia.utils.MeshObject;


public class BackLeftArrow extends MeshObject
{

    private Buffer mVertBuff;
    private Buffer mTexCoordBuff;
    private Buffer mNormBuff;

    private int verticesNumber = 0;


    public BackLeftArrow()
    {
        setVerts();
        setTexCoords();
        setNorms();
    }


    private void setVerts()
    {
        double[] TEAPOT_VERTS = {
                // f 1/1/1 3/2/1 4/3/1
                0.0849595252965498, 0.308584560566991, -0.602623482416644,
                0.354373439322039, 0.255389659150796, -0.524207360380883,
                0.0939700000993858, 0.255389659150796, -0.625734367287933,
                // f 4/4/2 12/5/2 16/6/2
                0.0939700000993858, 0.255389659150796, -0.625734367287933,
                -0.143386398111749, -0.0567708782749679, 0.00436405032554773,
                -0.147642019185146, -0.0316467398133009, 0.0152793099755636,
                // f 6/7/3 10/8/3 15/9/3
                0.250675477909136, 0.0262757665097095, 0.200697477713392,
                0.0940810357606571, -0.00652273298845805, 0.122097656489363,
                -0.0289083680673549, -0.00652266717004591, 0.0741461788758834,
                // f 7/10/4 11/11/4 14/12/4
                0.270434625960875, -0.0903763773243197, 0.15001730036963,
                0.102592343725863, -0.0567709440933801, 0.100267071370919,
                0.0983366568340541, -0.031646805631713, 0.111182331020935,
                // f 8/13/5 12/14/5 13/15/5
                -0.300609406096114, -0.0903762456874954, -0.0726234172564161,
                -0.143386398111749, -0.0567708782749679, 0.00436405032554773,
                -0.0203970601021489, -0.0567708782749679, 0.0523155279390274,
                // f 5/16/6 20/17/6 17/18/6
                -0.320368685784678, 0.0262758981465337, -0.0219433057310658,
                -0.149260954668424, -0.187461497051631, 0.370999591465116,
                -0.147122711913402, -0.186067594719441, 0.372530922641843,
                // f 11/19/7 13/20/7 3/21/7
                0.102592343725863, -0.0567709440933801, 0.100267071370919,
                -0.0203970601021489, -0.0567708782749679, 0.0523155279390274,
                0.354373439322039, 0.255389659150796, -0.524207360380883,
                // f 14/22/8 11/23/8 3/24/8
                0.0983366568340541, -0.031646805631713, 0.111182331020935,
                0.102592343725863, -0.0567709440933801, 0.100267071370919,
                0.354373439322039, 0.255389659150796, -0.524207360380883,
                // f 15/25/9 10/26/9 2/27/9
                -0.0289083680673549, -0.00652266717004591, 0.0741461788758834,
                0.0940810357606571, -0.00652273298845805, 0.122097656489363,
                0.345362964519203, 0.308584560566991, -0.501096541328006,
                // f 9/28/10 16/29/10 5/16/10
                -0.151897640258543, -0.00652266717004591, 0.0261946354439916,
                -0.147642019185146, -0.0316467398133009, 0.0152793099755636,
                -0.320368685784678, 0.0262758981465337, -0.0219433057310658,
                // f 17/18/11 6/7/11 5/16/11
                -0.147122711913402, -0.186067594719441, 0.372530922641843,
                0.250675477909136, 0.0262757665097095, 0.200697477713392,
                -0.320368685784678, 0.0262758981465337, -0.0219433057310658,
                // f 6/7/12 19/30/12 7/10/12
                0.250675477909136, 0.0262757665097095, 0.200697477713392,
                -0.14251943798711, -0.18697648117261, 0.37387078805767,
                0.270434625960875, -0.0903763773243197, 0.15001730036963,
                // f 7/10/13 20/17/13 8/13/13
                0.270434625960875, -0.0903763773243197, 0.15001730036963,
                -0.149260954668424, -0.187461497051631, 0.370999591465116,
                -0.300609406096114, -0.0903762456874954, -0.0726234172564161,
                // f 1/1/1 2/31/1 3/2/1
                0.0849595252965498, 0.308584560566991, -0.602623482416644,
                0.345362964519203, 0.308584560566991, -0.501096541328006,
                0.354373439322039, 0.255389659150796, -0.524207360380883,
                // f 16/6/2 9/32/2 1/33/2
                -0.147642019185146, -0.0316467398133009, 0.0152793099755636,
                -0.151897640258543, -0.00652266717004591, 0.0261946354439916,
                0.0849595252965498, 0.308584560566991, -0.602623482416644,
                // f 1/33/2 4/4/2 16/6/2
                0.0849595252965498, 0.308584560566991, -0.602623482416644,
                0.0939700000993858, 0.255389659150796, -0.625734367287933,
                -0.147642019185146, -0.0316467398133009, 0.0152793099755636,
                // f 15/9/3 9/28/3 5/16/3
                -0.0289083680673549, -0.00652266717004591, 0.0741461788758834,
                -0.151897640258543, -0.00652266717004591, 0.0261946354439916,
                -0.320368685784678, 0.0262758981465337, -0.0219433057310658,
                // f 5/16/3 6/7/3 15/9/3
                -0.320368685784678, 0.0262758981465337, -0.0219433057310658,
                0.250675477909136, 0.0262757665097095, 0.200697477713392,
                -0.0289083680673549, -0.00652266717004591, 0.0741461788758834,
                // f 14/12/4 10/8/4 6/7/4
                0.0983366568340541, -0.031646805631713, 0.111182331020935,
                0.0940810357606571, -0.00652273298845805, 0.122097656489363,
                0.250675477909136, 0.0262757665097095, 0.200697477713392,
                // f 6/7/4 7/10/4 14/12/4
                0.250675477909136, 0.0262757665097095, 0.200697477713392,
                0.270434625960875, -0.0903763773243197, 0.15001730036963,
                0.0983366568340541, -0.031646805631713, 0.111182331020935,
                // f 13/15/5 11/11/5 7/10/5
                -0.0203970601021489, -0.0567708782749679, 0.0523155279390274,
                0.102592343725863, -0.0567709440933801, 0.100267071370919,
                0.270434625960875, -0.0903763773243197, 0.15001730036963,
                // f 7/10/5 8/13/5 13/15/5
                0.270434625960875, -0.0903763773243197, 0.15001730036963,
                -0.300609406096114, -0.0903762456874954, -0.0726234172564161,
                -0.0203970601021489, -0.0567708782749679, 0.0523155279390274,
                // f 5/16/14 8/13/14 20/17/14
                -0.320368685784678, 0.0262758981465337, -0.0219433057310658,
                -0.300609406096114, -0.0903762456874954, -0.0726234172564161,
                -0.149260954668424, -0.187461497051631, 0.370999591465116,
                // f 4/34/7 3/21/7 13/20/7
                0.0939700000993858, 0.255389659150796, -0.625734367287933,
                0.354373439322039, 0.255389659150796, -0.524207360380883,
                -0.0203970601021489, -0.0567708782749679, 0.0523155279390274,
                // f 13/20/7 12/35/7 4/34/7
                -0.0203970601021489, -0.0567708782749679, 0.0523155279390274,
                -0.143386398111749, -0.0567708782749679, 0.00436405032554773,
                0.0939700000993858, 0.255389659150796, -0.625734367287933,
                // f 3/24/8 2/36/8 14/22/8
                0.354373439322039, 0.255389659150796, -0.524207360380883,
                0.345362964519203, 0.308584560566991, -0.501096541328006,
                0.0983366568340541, -0.031646805631713, 0.111182331020935,
                // f 10/37/8 14/22/8 2/36/8
                0.0940810357606571, -0.00652273298845805, 0.122097656489363,
                0.0983366568340541, -0.031646805631713, 0.111182331020935,
                0.345362964519203, 0.308584560566991, -0.501096541328006,
                // f 2/27/9 1/38/9 15/25/9
                0.345362964519203, 0.308584560566991, -0.501096541328006,
                0.0849595252965498, 0.308584560566991, -0.602623482416644,
                -0.0289083680673549, -0.00652266717004591, 0.0741461788758834,
                // f 9/39/9 15/25/9 1/38/9
                -0.151897640258543, -0.00652266717004591, 0.0261946354439916,
                -0.0289083680673549, -0.00652266717004591, 0.0741461788758834,
                0.0849595252965498, 0.308584560566991, -0.602623482416644,
                // f 8/13/10 5/16/10 16/29/10
                -0.300609406096114, -0.0903762456874954, -0.0726234172564161,
                -0.320368685784678, 0.0262758981465337, -0.0219433057310658,
                -0.147642019185146, -0.0316467398133009, 0.0152793099755636,
                // f 16/29/10 12/14/10 8/13/10
                -0.147642019185146, -0.0316467398133009, 0.0152793099755636,
                -0.143386398111749, -0.0567708782749679, 0.00436405032554773,
                -0.300609406096114, -0.0903762456874954, -0.0726234172564161,
                // f 17/18/11 18/40/11 6/7/11
                -0.147122711913402, -0.186067594719441, 0.372530922641843,
                -0.142673387253093, -0.186067594719441, 0.374265632712067,
                0.250675477909136, 0.0262757665097095, 0.200697477713392,
                // f 6/7/15 18/40/15 19/30/15
                0.250675477909136, 0.0262757665097095, 0.200697477713392,
                -0.142673387253093, -0.186067594719441, 0.374265632712067,
                -0.14251943798711, -0.18697648117261, 0.37387078805767,
                // f 7/10/16 19/30/16 20/17/16
                0.270434625960875, -0.0903763773243197, 0.15001730036963,
                -0.14251943798711, -0.18697648117261, 0.37387078805767,
                -0.149260954668424, -0.187461497051631, 0.370999591465116,
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
                // f 14/22/8 11/23/8 3/24/8
                0.6791, 1,
                0.7801, 1,
                0.7861, 0.4561,
                // f 15/25/9 10/26/9 2/27/9
                0.3425, 0,
                0.3131, 0,
                0.3114, 0.5477,
                // f 9/28/10 16/29/10 5/16/10
                0.7895, 0.7773,
                0.5141, 0.7759,
                0.9999, 0.6514,
                // f 17/18/11 6/7/11 5/16/11
                0.5452, 0.9139,
                0.8792, 0.8036,
                0.9999, 0.6514,
                // f 6/7/12 19/30/12 7/10/12
                0.8792, 0.8036,
                0.4572, 0.7181,
                0.0001, 0.9999,
                // f 7/10/13 20/17/13 8/13/13
                0.0001, 0.9999,
                0.5039, 0.8028,
                0.1394, 0.8373,
                // f 1/1/1 2/31/1 3/2/1
                0.6343, 0.2498,
                0.5721, 0.2498,
                0.5721, 0.4561,
                // f 16/6/2 9/32/2 1/33/2
                0.8931, 0.4561,
                0.7920, 0.4561,
                0.7861, 1,
                // f 1/33/2 4/4/2 16/6/2
                0.7861, 1,
                1.0000, 1,
                0.8931, 0.4561,
                // f 15/9/3 9/28/3 5/16/3
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
                // f 3/24/8 2/36/8 14/22/8
                0.7861, 0.4561,
                0.5721, 0.4561,
                0.6791, 1,
                // f 10/37/8 14/22/8 2/36/8
                0.5781, 1,
                0.6791, 1,
                0.5721, 0.4561,
                // f 2/27/9 1/38/9 15/25/9
                0.3114, 0.5477,
                0.3736, 0.5477,
                0.3425, 0,
                // f 9/39/9 15/25/9 1/38/9
                0.3718, 0,
                0.3425, 0,
                0.3736, 0.5477,
                // f 8/13/10 5/16/10 16/29/10
                0.1394, 0.8373,
                0.9999, 0.6514,
                0.5141, 0.7759,
                // f 16/29/10 12/14/10 8/13/10
                0.5141, 0.7759,
                0.2410, 0.8013,
                0.1394, 0.8373,
                // f 17/18/11 18/40/11 6/7/11
                0.5452, 0.9139,
                0.4974, 0.8508,
                0.8792, 0.8036,
                // f 6/7/15 18/40/15 19/30/15
                0.8792, 0.8036,
                0.4974, 0.8508,
                0.4572, 0.7181,
                // f 7/10/16 19/30/16 20/17/16
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
                0.329204154582646, 0.42260533331296, -0.844410656529729,
                0.329204154582646, 0.42260533331296, -0.844410656529729,
                0.329204154582646, 0.42260533331296, -0.844410656529729,
                // f 4/4/2 12/5/2 16/6/2
                -0.935085632519634, -0.00439993239555811, -0.354394554769499,
                -0.935085632519634, -0.00439993239555811, -0.354394554769499,
                -0.935085632519634, -0.00439993239555811, -0.354394554769499,
                // f 6/7/3 10/8/3 15/9/3
                0.325086276772483, 0.446081169080912, -0.833864799140491,
                0.325086276772483, 0.446081169080912, -0.833864799140491,
                0.325086276772483, 0.446081169080912, -0.833864799140491,
                // f 7/10/4 11/11/4 14/12/4
                0.334302505606669, 0.42260316742261, -0.842406313858984,
                0.334302505606669, 0.42260316742261, -0.842406313858984,
                0.334302505606669, 0.42260316742261, -0.842406313858984,
                // f 8/13/5 12/14/5 13/15/5
                0.33308731294587, 0.39888480676706, -0.854367457763289,
                0.33308731294587, 0.39888480676706, -0.854367457763289,
                0.33308731294587, 0.39888480676706, -0.854367457763289,
                // f 5/16/6 20/17/6 17/18/6
                -0.695311215460359, 0.459407410301293, 0.552708915266705,
                -0.695311215460359, 0.459407410301293, 0.552708915266705,
                -0.695311215460359, 0.459407410301293, 0.552708915266705,
                // f 11/19/7 13/20/7 3/21/7
                0.152804180015517, -0.907224817474328, -0.391910720864406,
                0.152804180015517, -0.907224817474328, -0.391910720864406,
                0.152804180015517, -0.907224817474328, -0.391910720864406,
                // f 14/22/8 11/23/8 3/24/8
                0.928219214336607, -0.00440009108282813, 0.372007700639106,
                0.928219214336607, -0.00440009108282813, 0.372007700639106,
                0.928219214336607, -0.00440009108282813, 0.372007700639106,
                // f 15/25/9 10/26/9 2/27/9
                -0.154197983103571, 0.905388157600347, 0.39559482565352,
                -0.154197983103571, 0.905388157600347, 0.39559482565352,
                -0.154197983103571, 0.905388157600347, 0.39559482565352,
                // f 9/28/10 16/29/10 5/16/10
                0.32409602498663, 0.422594816906356, -0.846389619094982,
                0.32409602498663, 0.422594816906356, -0.846389619094982,
                0.32409602498663, 0.422594816906356, -0.846389619094982,
                // f 17/18/11 6/7/11 5/16/11
                -0.207709635873517, 0.820338056365173, 0.532824718312037,
                -0.207709635873517, 0.820338056365173, 0.532824718312037,
                -0.207709635873517, 0.820338056365173, 0.532824718312037,
                // f 6/7/12 19/30/12 7/10/12
                0.506897632793582, -0.269798740042826, 0.818696176697782,
                0.506897632793582, -0.269798740042826, 0.818696176697782,
                0.506897632793582, -0.269798740042826, 0.818696176697782,
                // f 7/10/13 20/17/13 8/13/13
                0.095001092518846, -0.965211099991475, -0.243602801448325,
                0.095001092518846, -0.965211099991475, -0.243602801448325,
                0.095001092518846, -0.965211099991475, -0.243602801448325,
                // f 1/1/1 2/31/1 3/2/1
                0.329204154582646, 0.42260533331296, -0.844410656529729,
                0.329204154582646, 0.42260533331296, -0.844410656529729,
                0.329204154582646, 0.42260533331296, -0.844410656529729,
                // f 16/6/2 9/32/2 1/33/2
                -0.935085632519634, -0.00439993239555811, -0.354394554769499,
                -0.935085632519634, -0.00439993239555811, -0.354394554769499,
                -0.935085632519634, -0.00439993239555811, -0.354394554769499,
                // f 1/33/2 4/4/2 16/6/2
                -0.935085632519634, -0.00439993239555811, -0.354394554769499,
                -0.935085632519634, -0.00439993239555811, -0.354394554769499,
                -0.935085632519634, -0.00439993239555811, -0.354394554769499,
                // f 15/9/3 9/28/3 5/16/3
                0.325086276772483, 0.446081169080912, -0.833864799140491,
                0.325086276772483, 0.446081169080912, -0.833864799140491,
                0.325086276772483, 0.446081169080912, -0.833864799140491,
                // f 5/16/3 6/7/3 15/9/3
                0.325086276772483, 0.446081169080912, -0.833864799140491,
                0.325086276772483, 0.446081169080912, -0.833864799140491,
                0.325086276772483, 0.446081169080912, -0.833864799140491,
                // f 14/12/4 10/8/4 6/7/4
                0.334302505606669, 0.42260316742261, -0.842406313858984,
                0.334302505606669, 0.42260316742261, -0.842406313858984,
                0.334302505606669, 0.42260316742261, -0.842406313858984,
                // f 6/7/4 7/10/4 14/12/4
                0.334302505606669, 0.42260316742261, -0.842406313858984,
                0.334302505606669, 0.42260316742261, -0.842406313858984,
                0.334302505606669, 0.42260316742261, -0.842406313858984,
                // f 13/15/5 11/11/5 7/10/5
                0.33308731294587, 0.39888480676706, -0.854367457763289,
                0.33308731294587, 0.39888480676706, -0.854367457763289,
                0.33308731294587, 0.39888480676706, -0.854367457763289,
                // f 7/10/5 8/13/5 13/15/5
                0.33308731294587, 0.39888480676706, -0.854367457763289,
                0.33308731294587, 0.39888480676706, -0.854367457763289,
                0.33308731294587, 0.39888480676706, -0.854367457763289,
                // f 5/16/14 8/13/14 20/17/14
                -0.927996241622832, -0.269098910151621, 0.25769895632134,
                -0.927996241622832, -0.269098910151621, 0.25769895632134,
                -0.927996241622832, -0.269098910151621, 0.25769895632134,
                // f 4/34/7 3/21/7 13/20/7
                0.152804180015517, -0.907224817474328, -0.391910720864406,
                0.152804180015517, -0.907224817474328, -0.391910720864406,
                0.152804180015517, -0.907224817474328, -0.391910720864406,
                // f 13/20/7 12/35/7 4/34/7
                0.152804180015517, -0.907224817474328, -0.391910720864406,
                0.152804180015517, -0.907224817474328, -0.391910720864406,
                0.152804180015517, -0.907224817474328, -0.391910720864406,
                // f 3/24/8 2/36/8 14/22/8
                0.928219214336607, -0.00440009108282813, 0.372007700639106,
                0.928219214336607, -0.00440009108282813, 0.372007700639106,
                0.928219214336607, -0.00440009108282813, 0.372007700639106,
                // f 10/37/8 14/22/8 2/36/8
                0.928219214336607, -0.00440009108282813, 0.372007700639106,
                0.928219214336607, -0.00440009108282813, 0.372007700639106,
                0.928219214336607, -0.00440009108282813, 0.372007700639106,
                // f 2/27/9 1/38/9 15/25/9
                -0.154197983103571, 0.905388157600347, 0.39559482565352,
                -0.154197983103571, 0.905388157600347, 0.39559482565352,
                -0.154197983103571, 0.905388157600347, 0.39559482565352,
                // f 9/39/9 15/25/9 1/38/9
                -0.154197983103571, 0.905388157600347, 0.39559482565352,
                -0.154197983103571, 0.905388157600347, 0.39559482565352,
                -0.154197983103571, 0.905388157600347, 0.39559482565352,
                // f 8/13/10 5/16/10 16/29/10
                0.32409602498663, 0.422594816906356, -0.846389619094982,
                0.32409602498663, 0.422594816906356, -0.846389619094982,
                0.32409602498663, 0.422594816906356, -0.846389619094982,
                // f 16/29/10 12/14/10 8/13/10
                0.32409602498663, 0.422594816906356, -0.846389619094982,
                0.32409602498663, 0.422594816906356, -0.846389619094982,
                0.32409602498663, 0.422594816906356, -0.846389619094982,
                // f 17/18/11 18/40/11 6/7/11
                -0.207709635873517, 0.820338056365173, 0.532824718312037,
                -0.207709635873517, 0.820338056365173, 0.532824718312037,
                -0.207709635873517, 0.820338056365173, 0.532824718312037,
                // f 6/7/15 18/40/15 19/30/15
                0.507013436034085, -0.269807149984213, 0.818621693762331,
                0.507013436034085, -0.269807149984213, 0.818621693762331,
                0.507013436034085, -0.269807149984213, 0.818621693762331,
                // f 7/10/16 19/30/16 20/17/16
                0.139898661875699, -0.97659065895502, -0.163398437101424,
                0.139898661875699, -0.97659065895502, -0.163398437101424,
                0.139898661875699, -0.97659065895502, -0.163398437101424,
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