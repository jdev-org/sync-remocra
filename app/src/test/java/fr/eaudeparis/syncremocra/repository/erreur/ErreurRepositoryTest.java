package fr.eaudeparis.syncremocra.repository.erreur;

import static org.junit.Assert.assertFalse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.jooq.DSLContext;
import org.junit.Test;

public class ErreurRepositoryTest {

  @Test
  public void shouldIgnoreUnknownErrorCodeWithoutTryingToInsert() {
    TrackingHandler handler = new TrackingHandler();
    DSLContext context =
        (DSLContext)
            Proxy.newProxyInstance(
                DSLContext.class.getClassLoader(), new Class<?>[] {DSLContext.class}, handler);

    ErreurRepository repository = new ErreurRepository(context);

    repository.addError("HTML_ERROR", null, 51514L);

    assertFalse(handler.insertCalled);
  }

  private static final class TrackingHandler implements InvocationHandler {
    private boolean insertCalled;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
      String methodName = method.getName();

      if ("selectFrom".equals(methodName) || "where".equals(methodName)) {
        return proxyFor(method.getReturnType(), this);
      }
      if ("fetchOneInto".equals(methodName)) {
        return null;
      }
      if ("insertInto".equals(methodName)) {
        insertCalled = true;
        return proxyFor(method.getReturnType(), this);
      }
      if ("toString".equals(methodName)) {
        return "TrackingProxy";
      }
      if ("hashCode".equals(methodName)) {
        return System.identityHashCode(proxy);
      }
      if ("equals".equals(methodName)) {
        return proxy == args[0];
      }

      return defaultValue(method.getReturnType());
    }

    private static Object proxyFor(Class<?> type, InvocationHandler handler) {
      if (type == null || !type.isInterface()) {
        return defaultValue(type);
      }
      return Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type}, handler);
    }

    private static Object defaultValue(Class<?> type) {
      if (type == null || !type.isPrimitive()) {
        return null;
      }
      if (boolean.class.equals(type)) {
        return false;
      }
      if (byte.class.equals(type)) {
        return (byte) 0;
      }
      if (short.class.equals(type)) {
        return (short) 0;
      }
      if (int.class.equals(type)) {
        return 0;
      }
      if (long.class.equals(type)) {
        return 0L;
      }
      if (float.class.equals(type)) {
        return 0F;
      }
      if (double.class.equals(type)) {
        return 0D;
      }
      if (char.class.equals(type)) {
        return '\0';
      }
      return null;
    }
  }
}
