package com.openrangelabs.services.operations.dao;

import com.openrangelabs.services.operations.dao.mappers.*;
import com.openrangelabs.services.operations.model.*;
import com.openrangelabs.services.operations.dao.mappers.*;
import com.openrangelabs.services.operations.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.openrangelabs.services.config.OperationsDBConfig.OPS_NAMEDJDBCTEMPLATE;

@Repository
public class OperationsBloxopsDAO {

    @Autowired
    @Qualifier(OPS_NAMEDJDBCTEMPLATE)
    NamedParameterJdbcTemplate jdbcTemplate;

    public List<Link> getLinks() throws EmptyResultDataAccessException {
        String sql = "select l.id, l.url,l.description, l.name ,l.department ,l.approved " +
                " from links l ";

        MapSqlParameterSource params = new MapSqlParameterSource();

        return jdbcTemplate.query(sql, params, new LinkMapper());
    }

    public int updateLink(LinksUpdateRequest linksUpdateRequest) {
        String sql = "update links " +
                " set department = :department ,description = :description ,name = :name ,url = :url " +
                " where id = :id;";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("department", linksUpdateRequest.getDepartment());
        params.addValue("description", linksUpdateRequest.getDescription());
        params.addValue("url", linksUpdateRequest.getUrl());
        params.addValue("name", linksUpdateRequest.getName());
        params.addValue("id", linksUpdateRequest.getId());
        return jdbcTemplate.update(sql, params);

    }

    public int addLink(LinksUpdateRequest linksUpdateRequest) {
        String sql = "INSERT INTO links (department, description, url, name )" +
                "VALUES (:department, :description, :url, :name)";
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("department", linksUpdateRequest.getDepartment());
        params.addValue("description", linksUpdateRequest.getDescription());
        params.addValue("url", linksUpdateRequest.getUrl());
        params.addValue("name", linksUpdateRequest.getName());
        params.addValue("id", linksUpdateRequest.getId());

        return jdbcTemplate.update(sql, params);

    }

    public int deleteLink(LinksUpdateRequest linksUpdateRequest) {
        String sql = "DELETE from links " +
                " where id = :id;";
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("id", linksUpdateRequest.getId());

        return jdbcTemplate.update(sql, params);

    }

    public List<Alert> getAlerts() {
        String sql = "select a.displayed , a.active, a.message,a.created_dt , a.id " +
                " from alerts a ";

        MapSqlParameterSource params = new MapSqlParameterSource();

        return jdbcTemplate.query(sql, params, new AlertMapper());
    }

    public List<Alert> getAlert() {
        String sql = "select a.displayed , a.active, a.message,a.created_dt ,a.id " +
                " from alerts a " +
                " where a.active = true" +
                " limit 1 ";

        MapSqlParameterSource params = new MapSqlParameterSource();

        return jdbcTemplate.query(sql, params, new AlertMapper());
    }

    public int addAlert(Alert alert) {
        String sql = "INSERT INTO alerts (displayed, active, message, created_dt )" +
                "VALUES (:displayed, :active, :message, now())";
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("displayed", alert.getDisplayed());
        params.addValue("active", alert.isActive());
        params.addValue("message", alert.getMessage());

        return jdbcTemplate.update(sql, params);
    }

    public int updateAlert(Alert alert) {
        String sql = "update alerts " +
                " set displayed = :displayed ,message = :message ,active = :active ,created_dt = :created_dt " +
                " where id = :id;";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("message", alert.getMessage());
        params.addValue("displayed", alert.getDisplayed());
        params.addValue("active", alert.isActive());
        params.addValue("created_dt", alert.getCreated_dt());
        params.addValue("id", alert.getId());

        return jdbcTemplate.update(sql, params);

    }


    public int addSubscription(Subscription subscription) {
        String sql = "INSERT INTO subscriptions (notes, name, expiration_dt, created_dt,username , url) " +
                "VALUES (:notes, :name ,:expiration_dt, now() , :username ,:url)";
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("notes", subscription.getNotes());
        params.addValue("name", subscription.getName());
        params.addValue("expiration_dt", subscription.getExpiration_dt());
        params.addValue("username", subscription.getUsername());
        params.addValue("url", subscription.getUrl());

        return jdbcTemplate.update(sql, params);

    }

    public List<Subscription> getSubscriptions() {
        String sql = "select s.created_dt , s.notes, s.name , s.expiration_dt , s.username ,s.url , s.id  " +
                " from subscriptions s ";

        MapSqlParameterSource params = new MapSqlParameterSource();

        return jdbcTemplate.query(sql, params, new SubscriptionMapper());
    }

    public int deleteSubscription(Subscription subscription) {
        String sql = "DELETE from subscriptions " +
                " where id = :id;";
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("id", subscription.getId());

        return jdbcTemplate.update(sql, params);

    }

    public int updateSubscription(Subscription subscription) {
        String sql = "update subscriptions " +
                " set notes = :notes ,name = :name ,expiration_dt = :expiration_dt ,username = :username ,url = :url " +
                " where name = :name;";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("notes", subscription.getNotes());
        params.addValue("name", subscription.getName());
        params.addValue("expiration_dt", subscription.getExpiration_dt());
        params.addValue("username", subscription.getUsername());
        params.addValue("url", subscription.getUrl());

        return jdbcTemplate.update(sql, params);

    }

    public List<Tag> getTags(int linkId) {
        String sql = "select term , link_id  " +
                " from tags " +
                " where link_id = :linkId; ";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("linkId", linkId);

        return jdbcTemplate.query(sql, params, new TagMapper());
    }

    public int addTag(TagRequest tagRequest) {
        String sql = "INSERT INTO tags (term, link_id )" +
                "VALUES (:term, :linkId);";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("term", tagRequest.getTag());
        params.addValue("linkId", tagRequest.getId());

        return jdbcTemplate.update(sql, params);

    }

    public int removeTag(TagRequest tagRequest) {
        String sql = "DELETE FROM tags " +
                "WHERE link_id = :linkId AND term = :term;";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("term", tagRequest.getTag());
        params.addValue("linkId", tagRequest.getId());

        return jdbcTemplate.update(sql, params);

    }

    public List<Timesheet> getTimesheets(String startDate , String endDate) {
        String sql = "SELECT t.entry_date,\n" +
                "       t.author_name,\n" +
                "       t.id,\n" +
                "       SUM(t.hours)::double precision AS hours,\n" +
                "       t.title,\n" +
                "       t.epic_name AS project,\n" +
                "       t.epic_classification AS project_class,\n" +
                "       t.classification AS task_class  " +
                " FROM timesheets t\n" +
                " WHERE t.entry_date >= '" + startDate +"'::date\n" +
                "  AND t.entry_date < '"+ endDate +"'::date\n" +
                " GROUP BY t.entry_date,\n" +
                "         t.author_name,\n" +
                "         t.title,\n" +
                "         t.epic_name,\n" +
                "         t.epic_classification,\n" +
                "         t.classification,\n" +
                "         t.id\n" +
                " ORDER BY t.entry_date ASC; ";

        return jdbcTemplate.query(sql,  new TimesheetMapper());
    }
}
