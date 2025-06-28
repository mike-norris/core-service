package com.openrangelabs.services.eula.dao;

import com.openrangelabs.services.eula.dao.mappers.EulaMapper;
import com.openrangelabs.services.eula.dao.mappers.EulaUserDetailsMapper;
import com.openrangelabs.services.eula.entity.Eula;
import com.openrangelabs.services.eula.model.EulaUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.openrangelabs.services.config.BloxopsDBConfig.BLOXOPS_NAMEDJDBCTEMPLATE;

@Repository
public class EulaBloxopsDAO {

    @Autowired
    @Qualifier(BLOXOPS_NAMEDJDBCTEMPLATE)
    NamedParameterJdbcTemplate jdbcTemplate;
    private static String USER_ID = "user_id";

    public List<EulaUserDetails> getEulaUserDetails(Integer userId, String latestEulaVersion) {

        String sql = "select eula_id , created_date_time , version, status , updated_date_time" +
                " from eula_status" +
                " where" +
                " user_id =:user_id" +
                " AND version =:latestEulaVersion ;";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(USER_ID, userId);
        params.addValue("latestEulaVersion", latestEulaVersion);
        return jdbcTemplate.query(sql, params, new EulaUserDetailsMapper());
    }

    public int saveStatus(String version , String status , String browserInfo , int userId , int eulaId) {

        String sql = "INSERT INTO eula_status  (version ,status,browser_info , user_id ,created_date_time , eula_id)" +
                "VALUES (:version, :status ,:browserInfo ,:user_id ,CURRENT_TIMESTAMP , :eulaId)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("version", version);
        params.addValue("status", status);
        params.addValue("browserInfo", browserInfo);
        params.addValue(USER_ID, userId);
        params.addValue("eulaId", eulaId);

       return jdbcTemplate.update(sql, params);
    }

    public int updateStatus(String version , String status , String browserInfo , int userId) {

        String sql = "UPDATE eula_status" +
                " SET status = :status , updated_date_time = CURRENT_TIMESTAMP , browser_info = :browserInfo" +
                " WHERE user_id = :user_id AND version = :version";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("version", version);
        params.addValue("status", status);
        params.addValue("browserInfo", browserInfo);
        params.addValue(USER_ID, userId);

        return jdbcTemplate.update(sql, params);
    }

    public List<Eula> getAllEulas() {
        String sql = "select eula_id , created_date_time , published_date , version , pdf_location , html_location , updated_date_time" +
                " from eula;";

        MapSqlParameterSource params = new MapSqlParameterSource();
        return jdbcTemplate.query(sql, params, new EulaMapper());
    }

    public Eula getEulaByVersion(String latestEulaVersion) {
        String sql = "select eula_id , created_date_time , published_date , version , pdf_location , html_location , updated_date_time" +
                " from eula"+
                " where version =:latestEulaVersion;";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("latestEulaVersion", latestEulaVersion);
        return jdbcTemplate.query(sql, params, new EulaMapper()).get(0);
    }
}
