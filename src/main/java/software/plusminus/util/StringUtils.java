package software.plusminus.util;

import com.google.common.base.CaseFormat;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtils {
    
    public String enumNameToCamelCase(Enum<?> e) {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, e.name());
    }
    
}
