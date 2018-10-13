package ud.susel.tool.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * Provides reflection based utility methods.
 */
public class ReflectionUtils {

    /**
     * Indicates if a given method is a public class instance method or not.
     *
     * @param method the method to assert.
     * @return true if a given method is a public class instance method. Otherwise, returns false.
     */
    public static boolean isPublicInstance(Method method) {
        int mod = method.getModifiers();
        return Modifier.isPublic(mod) && !Modifier.isStatic(mod);
    }

    /**
     * Ensures that the given method takes only a single parameter.
     *
     * @param method the method to assert.
     */
    public static void requiresSingleParam(Method method) {
        requiresParamCount(method, 1);
    }

    /**
     * Ensures that the given method has the specified number of parameters.
     *
     * @param method the method to assert.
     * @param count the number of parameters that the method signature should contain.
     */
    public static void requiresParamCount(Method method, int count) {
        if (method.getParameterCount() != count) {
            throw new RuntimeException(String.format("Method (%s) signature is required to have %d parameter(s)", method.getName(), count));
        }
    }

    /**
     * Ensures that the method's parameter has the desired parameter type.
     *
     * @param method the method to assert.
     * @param parameter the parameter to assert.
     * @param paramType the parameter type to match.
     */
    public static void requiresParam(Method method, Parameter parameter, Class<?> paramType) {
        if (parameter.getType() != paramType) {
            throw new RuntimeException(String.format(
                    "Method (%s) signature is required to have a %s type for parameter (%s)",
                    method.getName(),
                    paramType.getName(),
                    parameter.getName()));
        }
    }

    /**
     * Ensures that the given parameter type is not an array class.
     *
     * @param method the method on which the parameter is defined.
     * @param parameter the parameter to assert.
     */
    public static void disallowArrayParam(Method method, Parameter parameter) {
        if (parameter.getType().isArray()) {
            throw new RuntimeException(String.format(
                    "Method (%s) signature cannot have an array parameter (%s)",
                    method.getName(),
                    parameter.getName()));
        }
    }

    /**
     * Determine the actual type of the method's parameter.
     *
     * Note: handles simple parameters--the type of {@code List<T>} is considered to be a regular
     * class, not a nested {@link java.util.Collection}.
     *
     * @param method the method on which the parameter is defined.
     * @param paramIndex the parameter index in the method signature whose actual type is to be determined.
     * @return the actual type of the given method's parameter wrapped in a holder.
     */
    public static ParamTypeHolder paramType(Method method, int paramIndex) {
        var parameter = method.getParameters()[paramIndex];

        if (parameter.getType() == List.class) {
            var types = method.getGenericParameterTypes();
            var parameterizedType = (ParameterizedType) types[paramIndex];

            return new ParamTypeHolder((Class<?>) parameterizedType.getActualTypeArguments()[0], true);

        } else {
            return new ParamTypeHolder(parameter.getType(), false);
        }
    }

    public static class ParamTypeHolder {
        private Class<?> actualType;
        private  boolean isList;

        public ParamTypeHolder(Class<?> actualType, boolean isList) {
            this.actualType = actualType;
            this.isList = isList;
        }

        public Class<?> actualType() {
            return actualType;
        }

        public boolean isList() {
            return isList;
        }
    }
}
