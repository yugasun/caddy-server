package com.example.caddyserver.config;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author yugasun
 * @date 2024/1/9
 **/
@Schema(name = "ConfigType", description = "ConfigType", enumAsRef = true)
public enum ConfigType {
    RESERVE_PROXY,

    CUSTOM_RESPOND,

    FILE_SERVER;

    public static String getConfig(ConfigType type) {
        return switch (type) {
            case RESERVE_PROXY -> """
                    ###START###
                    ${domain} {
                        import favicon
                        import encode
                        import cache
                        import logs ${name}
                        ${larkAuth}
                            
                        handle /* {
                            reverse_proxy http://${ip}:${port} {
                                header_up Host {host}
                                header_up X-Real-IP {remote_host}
                            }
                        }
                    }
                    ###END###
                                        """;
            case CUSTOM_RESPOND -> """
                    ###START###
                    ${domain} {
                        import favicon
                        import encode
                        import cache
                        import logs ${name}
                        ${larkAuth}
                            
                        handle /* {
                            respond "${respond}"
                        }
                    }
                    ###END###
                                """;
            case FILE_SERVER -> """
                    ###START###
                    ${domain} {
                         import favicon
                         import encode
                         import cache
                         import logs ${name}
                         ${larkAuth}
                             
                         handle /* {
                                root * ${root}
                                file_server
                         }
                     }
                     ###END###
                     """;
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }
}
