package fr.eaudeparis.syncremocra.db.binding;

import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Types;
import org.jooq.Binding;
import org.jooq.BindingGetResultSetContext;
import org.jooq.BindingGetSQLInputContext;
import org.jooq.BindingGetStatementContext;
import org.jooq.BindingRegisterContext;
import org.jooq.BindingSQLContext;
import org.jooq.BindingSetSQLOutputContext;
import org.jooq.BindingSetStatementContext;
import org.jooq.Converter;
import org.jooq.impl.DSL;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKBReader;
import org.locationtech.jts.io.WKBWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PointBinding implements Binding<Object, Point> {
  /** */
  private static final long serialVersionUID = -6942527990899130875L;

  private static final Logger logger = LoggerFactory.getLogger(PointBinding.class);

  private final transient WKBReader reader;
  private final transient WKBWriter writer;

  public PointBinding() {
    // TODO: inject this
    reader = new WKBReader();
    writer = new WKBWriter(3 /* outputDimension */, true /* includeSRID */);
  }
  // The converter does all the work
  @Override
  public Converter<Object, Point> converter() {
    return new PointConverter();
  }
  // Rending a bind variable for the binding context's value and casting it to the Point type
  @Override
  public void sql(BindingSQLContext<Point> ctx) {
    Object value = ctx.convert(converter()).value();
    if (value == null) {
      ctx.render().visit(DSL.val(value)).sql("::geometry");
      return;
    }
    ctx.render().sql("ST_GeomFromEWKB(").visit(DSL.val(value)).sql(")");
  }
  // Registering BLOB types for JDBC CallableStatement OUT parameters
  @Override
  public void register(BindingRegisterContext<Point> ctx) throws SQLException {
    ctx.statement().registerOutParameter(ctx.index(), Types.BLOB);
  }
  // Converting the Point to a byte[] value and setting that on a JDBC PreparedStatement
  @Override
  public void set(BindingSetStatementContext<Point> ctx) throws SQLException {
    ctx.statement().setBytes(ctx.index(), (byte[]) ctx.convert(converter()).value());
  }
  // Getting a String value from a JDBC ResultSet and converting that to a Point
  @Override
  public void get(BindingGetResultSetContext<Point> ctx) throws SQLException {
    ctx.convert(converter()).value(ctx.resultSet().getString(ctx.index()));
  }
  // Getting a String value from a JDBC CallableStatement and converting that to a Point
  @Override
  public void get(BindingGetStatementContext<Point> ctx) throws SQLException {
    ctx.convert(converter()).value(ctx.statement().getString(ctx.index()));
  }
  // Setting a value on a JDBC SQLOutput (useful for Oracle OBJECT types)
  @Override
  public void set(BindingSetSQLOutputContext<Point> ctx) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }
  // Getting a value from a JDBC SQLInput (useful for Oracle OBJECT types)
  @Override
  public void get(BindingGetSQLInputContext<Point> ctx) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  private class PointConverter implements Converter<Object, Point> {
    /** */
    private static final long serialVersionUID = 3437125002624031551L;

    @Override
    public Point from(Object t) {
      try {
        return t == null ? null : (Point) reader.read(WKBReader.hexToBytes((String) t));
      } catch (ParseException e) {
        logger.error("Not a valid Point : " + ((String) t), e);
        throw new IllegalArgumentException("Not a valid Point");
      }
    }

    @Override
    public Object to(Point u) {
      return u == null ? null : writer.write(u);
    }

    @Override
    public Class<Object> fromType() {
      return Object.class;
    }

    @Override
    public Class<Point> toType() {
      return Point.class;
    }
  }
}
