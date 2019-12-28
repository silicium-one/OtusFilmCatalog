package com.silicium.otusfilmcatalog.logic.model;

public interface IOnBackPressedListener {
    /**
     * Если вернуть ИСТИНА, то нажатие кнопки "назад" обрабтано. Если вернуть ЛОЖЬ, то требуется обработка выше по стэку.
     * @return ИСТИНА, если нажание кнопки back обработано и ЛОЖЬ в противном случае
     */
    boolean onBackPressed();
}
