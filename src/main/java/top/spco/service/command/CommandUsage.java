/*
 * Copyright 2023 SpCo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package top.spco.service.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 表示命令的一个用法。
 *
 * @author SpCo
 * @version 1.0.0
 * @since 1.0.0
 */
public class CommandUsage {
    public final String name;
    public final List<CommandParam> params = new ArrayList<>();
    private final String label;
    private boolean hasTarget;

    /**
     * 注意: 此方法仅供内部使用, <b>不应该</b>被外部调用<p>
     * 如需创建一个用法，请用{@link #CommandUsage(String, String, CommandParam...)}
     */
    public CommandUsage(String label, String name, List<CommandParam> params) {
        this.name = name;
        this.label = label;
        this.params.addAll(params);
    }

    public CommandUsage(String label, String name, CommandParam... params) {
        List<String> paramName = new ArrayList<>();
        int index = 0;
        this.hasTarget = false;
        for (CommandParam param : params) {
            if (paramName.contains(param.name)) {
                throw new IllegalArgumentException("Duplicate command parameter");
            }
            if (param.type == CommandParam.ParamType.OPTIONAL) {
                if (index + 1 != params.length) {
                    throw new IllegalArgumentException("Optional parameters must be the last ones");
                }
            }
            if (param.content == CommandParam.ParamContent.TARGET_USER_ID) {
                if (this.hasTarget) {
                    throw new IllegalArgumentException("Only one target_user_id parameter is allowed");
                }
                this.hasTarget = true;
            }
            paramName.add(param.name);
            this.params.add(param);
            index += 1;
        }
        this.label = label;
        this.name = name;
    }

    public boolean hasTargetParam() {
        return this.hasTarget;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("/").append(this.label);
        for (var param : this.params) {
            sb.append(" ").append(param.toString());
        }
        return this.name + "：" + sb;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommandUsage usage = (CommandUsage) o;
        return usage.toString().equals(toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(toString());
    }
}