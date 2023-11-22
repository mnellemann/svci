package biz.nellemann.svci;

import picocli.CommandLine;

public class DefaultProvider implements CommandLine.IDefaultValueProvider {

    @Override
    public String defaultValue(CommandLine.Model.ArgSpec argSpec) throws Exception {
        if(argSpec.isOption()) {
            switch (argSpec.paramLabel()) {
                case "<file>":
                    return getDefaultConfigFileLocation();
                default:
                    return null;
            }
        }
        return null;
    }

    private boolean isWindowsOperatingSystem() {
        String os = System.getProperty("os.name");
        return os.toLowerCase().startsWith("windows");
    }

    private String getDefaultConfigFileLocation() {
        String configFilePath;
        if(isWindowsOperatingSystem()) {
            configFilePath = System.getProperty("user.home") + "\\svci.toml";
        } else {
            configFilePath = "/etc/svci.toml";
        }
        return configFilePath;
    }
}
