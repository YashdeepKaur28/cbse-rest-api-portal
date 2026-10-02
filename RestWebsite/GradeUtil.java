package cbse;

public class GradeUtil {
    public static Object[] calculateFullResult(double math, double science,
                                                double english, double hindi, double sst) {
        double total = math + science + english + hindi + sst;
        double pct = total / 5.0;
        String grade;
        if (pct >= 91) grade = "A1";
        else if (pct >= 81) grade = "A2";
        else if (pct >= 71) grade = "B1";
        else if (pct >= 61) grade = "B2";
        else if (pct >= 51) grade = "C1";
        else if (pct >= 41) grade = "C2";
        else if (pct >= 33) grade = "D";
        else grade = "E";
        return new Object[]{total, pct, grade};
    }
}