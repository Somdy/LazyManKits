package rs.lazymankits.data;

import com.megacrit.cardcrawl.cards.AbstractCard;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class LMICardData {
    
    public abstract LMIValue<Integer>[] costs();
    public abstract LMIValue<Integer>[] damages();
    public abstract LMIValue<Integer>[] blocks();
    public abstract LMIValue<Integer>[] magics();
    public abstract LMIValue<Boolean>[] exhaust();
    public abstract LMIValue<Boolean>[] ethereal();
    public abstract LMIValue<Boolean>[] retain();
    public abstract LMIValue<Boolean>[] selfRetain();
    public abstract LMIValue<Boolean>[] purge();
    public abstract LMIValue<Boolean>[] innate();
    public abstract LMIValue<AbstractCard.CardRarity>[] rarities();
    public abstract LMIValue<AbstractCard.CardTarget>[] targets();
    public abstract LMIValue<AbstractCard.CardColor>[] colors();
    public abstract LMIValue<AbstractCard.CardType>[] types();
    public abstract LMIValue<List<AbstractCard.CardTags>>[] tags();
    
    @Nullable
    public static <T> T GetValue(@NotNull LMIValue<T>[] values) {
        for (LMIValue<T> v : values) {
            if (!v.isPrmt()) {
                return v.get();
            }
        }
        return null;
    }

    @Nullable
    public static <T> T GetPrmtValue(@NotNull LMIValue<T>[] values) {
        for (LMIValue<T> v : values) {
            if (v.isPrmt()) {
                return v.get();
            }
        }
        return null;
    }
    
    public class LMIValue<T> {
        final T value;
        final boolean isPrmt;
        
        public LMIValue(T value, boolean isPrmt) {
            this.value = value;
            this.isPrmt = isPrmt;
        }
        
        public LMIValue(T value) {
            this.value = value;
            this.isPrmt = false;
        }
        
        @NotNull
        @Contract(pure = true)
        public final LMIValue<T>[] same() {
            LMIValue<T>[] values = new LMIValue[2];
            values[0] = this;
            values[1] = new LMIValue<>(value, true);
            return values;
        }
        
        @NotNull
        @Contract(pure = true)
        public final LMIValue<T>[] prmt(T value) {
            LMIValue<T>[] values = new LMIValue[2];
            values[0] = this;
            values[1] = new LMIValue<>(value, true);
            return values;
        }
        
        @NotNull
        @Contract(pure = true)
        @SafeVarargs
        public final LMIValue<T>[] prmts(@NotNull T... values) {
            LMIValue<T>[] retvals = new LMIValue[values.length];
            retvals[0] = this;
            for (int i = 1; i < values.length; i++) {
                retvals[i] = new LMIValue<>(values[i], true);
            }
            return retvals;
        }

        public T get() {
            return value;
        }
        
        public boolean isPrmt() {
            return isPrmt;
        }
    }
}