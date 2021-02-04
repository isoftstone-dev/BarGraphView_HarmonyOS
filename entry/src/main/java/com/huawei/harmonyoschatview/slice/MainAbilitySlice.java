package com.huawei.harmonyoschatview.slice;

import com.huawei.harmonyoschatview.ResourceTable;
import com.huawei.histogramcomponent.view.HistogramComponent;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;

public class MainAbilitySlice extends AbilitySlice {
    private HistogramComponent bargraphview;
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        bargraphview = (HistogramComponent) findComponentById(ResourceTable.Id_bargraphview);
        // 设置每组柱状图之间的间距
        bargraphview.mLineSpaceWidth = 30;
        final int[][] data = {{182, 89, 78, 88}, {34, 85, 16, 96}, {86, 127, 45, 41},{54, 75, 54, 12}};
        final int[] colorData = {0xFF222233, 0xFFe67656, 0xFF188888,0xFF888888,0xFF888888};
        final String[] textData = {"一月份", "二月份", "三月份", "四月份"};
        bargraphview.setBarGraphData(data, colorData, textData);

    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}
