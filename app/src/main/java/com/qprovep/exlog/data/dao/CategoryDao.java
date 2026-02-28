package com.qprovep.exlog.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.qprovep.exlog.data.entity.Category;

import java.util.List;

@Dao
public interface CategoryDao {

    @Insert
    long insert(Category category);

    @Query("DELETE FROM categories WHERE name = :name")
    void deleteByName(String name);

    @Query("SELECT * FROM categories ORDER BY name ASC")
    LiveData<List<Category>> getAllCategories();

    @Query("SELECT * FROM categories ORDER BY name ASC")
    List<Category> getAllCategoriesSync();

    @Insert
    void insertAll(List<Category> categories);
}
