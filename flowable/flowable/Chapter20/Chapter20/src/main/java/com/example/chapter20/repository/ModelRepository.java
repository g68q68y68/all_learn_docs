package com.example.chapter20.repository;

import com.example.chapter20.domain.Model;
//import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public  interface ModelRepository extends JpaRepository<Model, String> {
    @Query("from Model as model where model.key = :key and model.modelType = :modelType")
    List<Model> findModelsByKeyAndType(@Param("key") String key, @Param("modelType") Integer modelType);

}