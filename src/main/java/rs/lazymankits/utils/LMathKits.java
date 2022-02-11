package rs.lazymankits.utils;

import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.helpers.Hitbox;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class LMathKits {
    
    public static double SciRound(double a, double reserved) {
        long bH = (long) a;
        long epd = (long) Math.pow(10, reserved);
        long nH = (long) ((a - bH) * epd);
        float nT = (float) ((a - bH) * epd - nH);
        int hT = (int) (nH % 10);
        int tH = (int) (nT * 10);
        if (tH > 5) {
            nH++;
        }
        else if (tH == 5) {
            if (hT % 2 != 0) nH++;
        }
        return bH + (double) nH / epd;
    }

    public static float SciRound(double a) {
        return (float) SciRound(a, 0);
    }

    public static long SciPercent(double a) {
        return (long) (SciRound(a, 2) * 100);
    }

    @NotNull
    public static float[] AssignMinAndMax(float a, float b) {
        float[] values = new float[] {0, 0};
        values[0] = Math.min(a, b);
        values[1] = Math.max(a, b);
        return values;
    }
    
    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    public static int[] Swap(int a, int b) {
        a = a ^ b;
        b = a ^ b;
        a = a ^ b;
        return new int[] {a, b};
    }

    /**
     * @return true if the given box interacts with the target circle
     * @param c the centre of the box
     * @param o the centre of the circle
     * @param h the half length of the box (half width or half height)
     * @param r the radius of the circle
     */
    public static boolean BoxInteractsCircle(@NotNull Vector2 c, @NotNull Vector2 o, @NotNull Vector2 h, float r) {
        Vector2 v = new Vector2(Math.abs(c.x - o.x), Math.abs(c.y - o.x));
        Vector2 u = new Vector2(Math.max(v.x - h.x, 0), Math.max(v.y - h.y, 0));
        return u.len2() <= r * r;
    }

    public static boolean BoxInteractsCircle(@NotNull Hitbox box, @NotNull Vector2 o, float r) {
        Vector2 c = new Vector2(box.cX, box.cY);
        Vector2 d = new Vector2(o.x - c.x, o.y - c.y);
        float[] values = AssignMinAndMax(box.height / 2, box.width / 2);
        Vector2 clp = d.clamp(values[0], values[1]);
        Vector2 p = c.add(clp);
        d = p.sub(o);
        return d.len2() < r * r;
    }
}