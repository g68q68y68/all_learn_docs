package com.bpm.example;

import com.bpm.example.common.util.FlowableEngineUtil;
import org.flowable.common.engine.api.management.TableMetaData;
import org.flowable.common.engine.api.management.TablePage;
import org.flowable.engine.impl.persistence.entity.HistoricProcessInstanceEntity;

import java.util.List;
import java.util.Map;

public class RunDemo10 extends FlowableEngineUtil {
    public static void main(String[] args) {
        RunDemo10 demo = new RunDemo10();
        demo.runDemo();
    }

    private void runDemo() {
        //初始化工作流引擎
        initFlowableEngineAndServices("flowable.cfg.xml");
        //获取数据表的记录数量
        Map<String, Long> tableCount = managementService.getTableCount();
        for (Map.Entry<String, Long> entry : tableCount.entrySet()) {
            System.out.println("表" + entry.getKey() + "的记录数为：" + entry.getValue());
        }
        //获取数据表的名称及元数据
        String tableName =
                managementService.getTableName(HistoricProcessInstanceEntity.class);
        System.out.println("HistoricProcessInstanceEntity对应的表名为：" + tableName);
        TableMetaData tableMetaData = managementService.getTableMetaData(tableName);
        List<String> columnNames = tableMetaData.getColumnNames();
        for (String columnName : columnNames) {
            System.out.println("--字段：" + columnName);
        }
        //获取表的分页查询对象
        TablePage tablePage = managementService.createTablePageQuery()
                .tableName(tableName)
                .orderAsc("START_TIME_")
                .listPage(1, 10);
        System.out.println(tableName + "的记录数为：" + tablePage.getTotal());
    }
}

