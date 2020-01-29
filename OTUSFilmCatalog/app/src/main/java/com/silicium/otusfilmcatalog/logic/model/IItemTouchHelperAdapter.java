package com.silicium.otusfilmcatalog.logic.model;

public interface IItemTouchHelperAdapter {
    /**
     * Перестановка элементов в адаптере местами (поддержка drag-n-drop)
     *
     * @param fromPosition откуда
     * @param toPosition   куда
     */
    void onItemMove(int fromPosition, int toPosition);


    /**
     * Удаление элемента
     *
     * @param position откуда
     */
    void onItemDismiss(int position);
}
