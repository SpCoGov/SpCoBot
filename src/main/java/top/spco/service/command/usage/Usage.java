/*
 * Copyright 2025 SpCo
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
package top.spco.service.command.usage;

import top.spco.service.command.usage.parameters.Parameter;
import top.spco.service.command.usage.parameters.TargetUserIdParameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 表示命令的一个用法。
 *
 * @author SpCo
 * @version 3.0.0
 * @since 1.0.0
 */
public class Usage {
    public final String name;
    private final List<Parameter<?>> params = new ArrayList<>();
    private final String label;
    private boolean hasTarget;

    public Usage(String label, String name, List<Parameter<?>> params) {
        List<String> paramName = new ArrayList<>();
        int index = 0;
        this.hasTarget = false;
        for (Parameter<?> param : params) {
            if (paramName.contains(param.getName())) {
                throw new IllegalArgumentException("Duplicate command parameter: " + param.getName());
            }
            if (param.isOptional()) {
                if (index + 1 != params.size()) {
                    throw new IllegalArgumentException("Optional parameters must be the last ones: " + this);
                }
            }
            if (param instanceof TargetUserIdParameter) {
                if (this.hasTarget) {
                    throw new IllegalArgumentException("Only one target_user_id parameter is allowed: " + this);
                }
                this.hasTarget = true;
            }
            paramName.add(param.getName());
            this.params.add(param);
            index += 1;
        }
        this.label = label;
        this.name = name;
    }

    /**
     * <b>请勿使用本方法构建命令用法。</b>
     * 请使用 {@link UsageBuilder} 或 {@link #Usage(String, String, List)}}
     */
    public Usage(String label, String name, List<Parameter<?>> params, Dummy ignored) {
        this.params.addAll(params);
        this.label = label;
        this.name = name;
    }

    public List<Parameter<?>> getParams() {
        return params;
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
        Usage usage = (Usage) o;
        return usage.toString().equals(toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(toString());
    }

    public static class Dummy {
    }
}