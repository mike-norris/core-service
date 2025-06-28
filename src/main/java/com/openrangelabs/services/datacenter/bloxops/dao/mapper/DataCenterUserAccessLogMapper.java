package com.openrangelabs.services.datacenter.bloxops.dao.mapper;

import com.openrangelabs.services.datacenter.entity.DataCenterUserAccessLog;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DataCenterUserAccessLogMapper implements RowMapper<DataCenterUserAccessLog> {


    @Override
    public DataCenterUserAccessLog mapRow(ResultSet rs, int rowNum) throws SQLException {
        DataCenterUserAccessLog  dataCenterUserAccessLog = new DataCenterUserAccessLog();

        dataCenterUserAccessLog.setGUID(rs.getString("guid"));
        dataCenterUserAccessLog.setPartitionID(rs.getInt("partition_id"));
        dataCenterUserAccessLog.setMessageDT(rs.getString("message_dt"));
        dataCenterUserAccessLog.setPersonnelGUID(rs.getString("personnel_guid"));
        dataCenterUserAccessLog.setPersonnelName(rs.getString("personnel_name"));
        dataCenterUserAccessLog.setAccessPointGUID(rs.getString("access_point_guid"));
        dataCenterUserAccessLog.setAccessPointName(rs.getString("access_point_name"));
        dataCenterUserAccessLog.setStatus(rs.getString("status"));
        dataCenterUserAccessLog.setMessageText(rs.getString("message_text"));
        dataCenterUserAccessLog.setMessageType(rs.getString("message_type"));
        dataCenterUserAccessLog.setCardNumber(rs.getString("card_number"));
        dataCenterUserAccessLog.setDataCenter(rs.getString("datacenter"));

        return dataCenterUserAccessLog;
    }
}
