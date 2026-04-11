package blocksmith.infra.blockloader;

import blocksmith.infra.blockloader.annotations.Block;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author joost
 */
public class SourceBlockScanner {

    private static final Logger LOGGER = Logger.getLogger(SourceBlockScanner.class.getName());

    private final ClassScanner classScanner;
    private List<SourceBlockInspector> inspectors;

    public SourceBlockScanner(ClassScanner classScanner) {
        this.classScanner = classScanner;
        rescan();
    }

    public void rescan() {
        var classes = classScanner.classes();
        this.inspectors = List.copyOf(loadEligible(classes));
    }

    public Collection<SourceBlockInspector> classes() {
        return inspectors;
    }

    private Collection<SourceBlockInspector> loadEligible(Collection<Class<?>> classes) {
        var annotated = filterByAnnotation(classes, Block.class);
        var eligible = new ArrayList<SourceBlockInspector>();
        for (var clazz : annotated) {
            try {
                var inspector = new SourceBlockInspector(clazz);
                eligible.add(inspector);
            } catch (Exception e) {
                LOGGER.finest(e.getMessage());
            }
        }
        return eligible;
    }

    public static List<Class<?>> filterByAnnotation(Collection<Class<?>> classes, Class<? extends Annotation> annotation) {
        var result = new ArrayList<Class<?>>();
        for (var clazz : classes) {
            if (clazz.isAnnotationPresent(annotation)) {
                result.add(clazz);
            }
        }
        return result;
    }
}
