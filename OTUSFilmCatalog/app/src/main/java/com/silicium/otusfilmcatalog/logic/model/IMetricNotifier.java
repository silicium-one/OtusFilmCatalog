package com.silicium.otusfilmcatalog.logic.model;

import androidx.annotation.NonNull;

public interface IMetricNotifier {
    String TOTAL_TAG = "total"; // Фильмов всего
    String FAVORITES_TAG = "favorites"; // Фильмов в избранном
    String SHARED_TAG = "shared"; // количество фильмов, которыми поделились
    String CARTOON_TAG = "cartoon"; // мультфильмов

    /**
     * Переслать наблюдателям все метрики
     */
    void notifyObserversDataChanged(); //TODO: замутить API для работы с наблюдатедями. Пока у меня только один фиджет, так что это лишнее

    /**
     * Увеличить метрику на 1 и оповестить наблюдателей
     * @param tag ID метрики
     */
    void increment(@NonNull String tag);

    /**
     * Уменьшить метрику на 1 и оповестить наблюдателей
     * @param tag ID метрики
     */
    void decrement(@NonNull String tag);
}
