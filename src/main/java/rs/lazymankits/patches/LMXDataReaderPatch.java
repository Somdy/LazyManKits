package rs.lazymankits.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import rs.lazymankits.data.LMXCardDataReader;
import rs.lazymankits.managers.LMCustomCardTagMgr;
import rs.lazymankits.managers.LMCustomRarityMgr;

public class LMXDataReaderPatch {
    @SpirePatch(clz = CardLibrary.class, method = "initialize")
    public static class ReadOnCardLibraryInit {
        @SpirePrefixPatch
        public static void Prefix() {
            LMXCardDataReader.StartReadDatas();
        }
    }
}