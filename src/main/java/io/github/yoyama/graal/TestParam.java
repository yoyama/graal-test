package io.github.yoyama.graal;

import java.util.Arrays;
import java.util.List;

public class TestParam
{
    private final String template;
    private final String params;

    public TestParam(String template, String params)
    {
        this.template = template;
        this.params = params;
    }

    public String getTemplate() { return template; }
    public String getParams() { return params; }

    public static TestParam of(String template, String params)
    {
        return new TestParam(template, params);
    }

    public static List<TestParam> TestData = Arrays.asList(
            TestParam.of("echo>: ${moment(session_time).format(\"YYYY-MM-DD HH:mm:ss Z\")}",
                    "{\"session_time\":1}"),
            TestParam.of("echo>: ${param0+param1+param2+param3+param4+param5+param6+param7+param8+param9+param10+param11+param12+param13+param14+param15+param16+param17+param18+param19+param20+param21+param22+param23+param24+param25+param26+param27+param28+param29+param30+param31+param32+param33+param34+param35+param36+param37+param38+param39+param40+param41+param42+param43+param44+param45+param46+param47+param48+param49+param50+param51+param52+param53+param54+param55+param56+param57+param58+param59+param60+param61+param62+param63+param64+param65+param66+param67+param68+param69+param70+param71+param72+param73+param74+param75+param76+param77+param78+param79+param80+param81+param82+param83+param84+param85+param86+param87+param88+param89+param90+param91+param92+param93+param94+param95+param96+param97+param98+param99}",
                    "{\"param0\": 0, \"param1\": 1, \"param2\": 2, \"param3\": 3, \"param4\": 4, \"param5\": 5, \"param6\": 6, \"param7\": 7, \"param8\": 8, \"param9\": 9, \"param10\": 10, \"param11\": 11, \"param12\": 12, \"param13\": 13, \"param14\": 14, \"param15\": 15, \"param16\": 16, \"param17\": 17, \"param18\": 18, \"param19\": 19, \"param20\": 20, \"param21\": 21, \"param22\": 22, \"param23\": 23, \"param24\": 24, \"param25\": 25, \"param26\": 26, \"param27\": 27, \"param28\": 28, \"param29\": 29, \"param30\": 30, \"param31\": 31, \"param32\": 32, \"param33\": 33, \"param34\": 34, \"param35\": 35, \"param36\": 36, \"param37\": 37, \"param38\": 38, \"param39\": 39, \"param40\": 40, \"param41\": 41, \"param42\": 42, \"param43\": 43, \"param44\": 44, \"param45\": 45, \"param46\": 46, \"param47\": 47, \"param48\": 48, \"param49\": 49, \"param50\": 50, \"param51\": 51, \"param52\": 52, \"param53\": 53, \"param54\": 54, \"param55\": 55, \"param56\": 56, \"param57\": 57, \"param58\": 58, \"param59\": 59, \"param60\": 60, \"param61\": 61, \"param62\": 62, \"param63\": 63, \"param64\": 64, \"param65\": 65, \"param66\": 66, \"param67\": 67, \"param68\": 68, \"param69\": 69, \"param70\": 70, \"param71\": 71, \"param72\": 72, \"param73\": 73, \"param74\": 74, \"param75\": 75, \"param76\": 76, \"param77\": 77, \"param78\": 78, \"param79\": 79, \"param80\": 80, \"param81\": 81, \"param82\": 82, \"param83\": 83, \"param84\": 84, \"param85\": 85, \"param86\": 86, \"param87\": 87, \"param88\": 88, \"param89\": 89, \"param90\": 90, \"param91\": 91, \"param92\": 92, \"param93\": 93, \"param94\": 94, \"param95\": 95, \"param96\": 96, \"param97\": 97, \"param98\": 98, \"param99\": 99}")
    );
}
