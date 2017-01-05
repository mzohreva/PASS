package pass.core.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum CompileOption
{
    WALL("CO_1", Category.COMMON, "Enable all warnings", "-Wall"),
    WEXTRA("CO_2", Category.COMMON, "Enable extra warnings", "-Wextra"),
    C90("CO_3", Category.C_DIALECTS, "C90", "--std=c90"),
    C99("CO_4", Category.C_DIALECTS, "C99", "--std=c99"),
    C11("CO_5", Category.C_DIALECTS, "C11", "--std=c11"),
    CPP98("CO_6", Category.CPP_DIALECTS, "C++98", "--std=c++98"),
    CPP03("CO_7", Category.CPP_DIALECTS, "C++03", "--std=c++03"),
    CPP11("CO_8", Category.CPP_DIALECTS, "C++11", "--std=c++11");
    // NOTE: Add new options to all_options list below

    public enum Category
    {
        COMMON("Common Options"),
        C_DIALECTS("C Dialects"),
        CPP_DIALECTS("C++ Dialects");

        private final String title;

        private Category(String title)
        {
            this.title = title;
        }

        public String getTitle()
        {
            return title;
        }
    }

    private final String id;
    private final Category category;
    private final String description;
    private final String args;

    private CompileOption(String id,
                          Category category,
                          String description,
                          String args)
    {
        this.id = id;
        this.category = category;
        this.description = description;
        this.args = args;
    }

    public String getId()
    {
        return id;
    }

    public Category getCategory()
    {
        return category;
    }

    public String getDescription()
    {
        return description;
    }

    public String getArgs()
    {
        return args;
    }

    private static final List<CompileOption> allOptions = new ArrayList<>();

    static {
        allOptions.add(WALL);
        allOptions.add(WEXTRA);
        allOptions.add(C90);
        allOptions.add(C99);
        allOptions.add(C11);
        allOptions.add(CPP98);
        allOptions.add(CPP03);
        allOptions.add(CPP11);
    }

    public static List<CompileOption> getOptionsForCategory(Category c)
    {
        List<CompileOption> sublist = new ArrayList<>();
        allOptions.stream()
                .filter((o) -> (o.category == c))
                .forEachOrdered((o) -> {
                    sublist.add(o);
                });
        return sublist;
    }

    public static List<CompileOption> getAllOptions()
    {
        return Collections.unmodifiableList(allOptions);
    }

    public static CompileOption getOptionById(String id)
    {
        for (CompileOption o : allOptions) {
            if (o.id.equals(id)) {
                return o;
            }
        }
        return null;
    }
}
