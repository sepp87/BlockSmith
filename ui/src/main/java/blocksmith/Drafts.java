package blocksmith;

import blocksmith.app.block.command.CopyBlocksCommand;
import blocksmith.app.block.command.DeselectAllBlocksCommand;
import blocksmith.app.block.command.PasteBlocksCommand;
import blocksmith.app.block.command.RemoveBlocksCommand;
import blocksmith.app.block.command.SelectAllBlocksCommand;
import blocksmith.app.command.Command;
import blocksmith.app.group.command.AddGroupCommand;
import blocksmith.app.outbound.WorkspaceRegistry;
import blocksmith.app.workspace.WorkspaceLifecycle;
import blocksmith.app.workspace.WorkspaceSession;
import blocksmith.app.workspace.command.NewFileCommand;
import blocksmith.app.workspace.command.SaveFileCommand;
import blocksmith.ui.align.command.AlignBottomCommand;
import blocksmith.ui.align.command.AlignHorizontallyCommand;
import blocksmith.ui.align.command.AlignLeftCommand;
import blocksmith.ui.align.command.AlignRightCommand;
import blocksmith.ui.align.command.AlignTopCommand;
import blocksmith.ui.align.command.AlignVerticallyCommand;
import blocksmith.ui.editor.navigation.command.ZoomToFitCommand;
import blocksmith.ui.workspace.WorkspaceFxHandle;
import blocksmith.ui.workspace.WorkspaceFxRegistry;
import blocksmith.ui.workspace.command.OpenFileCommand;
import blocksmith.ui.workspace.command.SaveAsFileCommand;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 *
 * @author joost
 */
public class Drafts {


    public static void getGenericTypeOfMethodParam() {
        var methods = Drafts.class.getMethods();

        for (var method : methods) {
            for (Parameter p : method.getParameters()) {

                if (p.getParameterizedType() instanceof ParameterizedType type) {
                    System.out.println(type.getActualTypeArguments()[0]);
                    System.out.println(type.getActualTypeArguments()[0].getClass());
                    System.out.println(type.getActualTypeArguments()[0].getTypeName());
                    if (type.getActualTypeArguments()[0] instanceof TypeVariable<?> tv) {
                        System.out.println(tv.getBounds().length);
                        System.out.println(tv.getBounds()[0]);

                    }

                }

                if (List.class.isAssignableFrom(p.getType())) {

                } else {

                }
            }
        }
    }

    public static <T> void test(Integer foo, List<T> test) {

    }

    public static void test2(List<Integer> test) {

    }
}
