package cz.zcu.kiv.crce.metadata.dao.filter;

/**
 * Date: 10.3.16
 *
 * @author Jakub Danek
 */
public enum Operator {
    AND("AND"),
    OR("OR");

    private final String sql;

    Operator(String sql) {
        this.sql = sql;
    }

    public String sql() {
        return sql;
    }
}
