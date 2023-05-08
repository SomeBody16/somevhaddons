package network.something.somevhaddons.api.util;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AnnotationScanner {

    @SuppressWarnings("unchecked")
    public static <T> T invokeStaticMethod(Method method) {
        try {
            method.setAccessible(true);
            return (T) method.invoke(null);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static @Nullable Method getFirstMethod(Class<? extends Annotation> annotation, Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotation)) {
                return method;
            }
        }
        return null;
    }

    public static List<Field> getFields(Class<? extends Annotation> annotation, Class<?> clazz) {
        var result = new ArrayList<Field>();
        for (var field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(annotation)) {
                result.add(field);
            }
        }
        return result;
    }

    public static List<Class<?>> getClasses(Class<? extends Annotation> annotation) {
        List<Class<?>> result = new ArrayList<>();

        try {
            Type annotationType = Type.getType(annotation);
            List<ModFileScanData> allScanData = ModList.get().getAllScanData();
            for (var scanData : allScanData) {
                var annotations = scanData.getAnnotations();
                for (var aData : annotations) {
                    if (Objects.equals(aData.annotationType(), annotationType)) {
                        var clazz = Class.forName(aData.memberName());
                        result.add(clazz);
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return result;
    }
}
