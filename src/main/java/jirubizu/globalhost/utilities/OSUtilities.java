package jirubizu.globalhost.utilities;

public class OSUtilities
{
    private static final String OperatingSystem = System.getProperty("os.name").toLowerCase();

    private static boolean isWindows()
    {
        return OperatingSystem.contains("win");
    }

    private static boolean isMac()
    {
        return OperatingSystem.contains("mac");
    }

    private static boolean isLinux()
    {
        return OperatingSystem.contains("nix") || OperatingSystem.contains("nux") || OperatingSystem.contains("aix");
    }

    public static OSEnum getOperatingSystem()
    {
        if (isLinux())
        {
            return OSEnum.LINUX;
        }
        else if (isMac())
        {
            return OSEnum.MACOS;
        }
        else if (isWindows())
        {
            return OSEnum.WINDOWS;
        }
        else
        {
            return null;
        }
    }
}

