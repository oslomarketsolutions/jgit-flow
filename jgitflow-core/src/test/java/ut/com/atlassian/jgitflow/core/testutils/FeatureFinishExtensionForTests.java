package ut.com.atlassian.jgitflow.core.testutils;

/*-
 * #%L
 * JGitFlow :: Core
 * %%
 * Copyright (C) 2017 Atlassian Pty, LTD, Ultreia.io
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.atlassian.jgitflow.core.extension.ExtensionCommand;
import com.atlassian.jgitflow.core.extension.FeatureFinishExtension;

import com.google.common.collect.Lists;

public class FeatureFinishExtensionForTests extends BaseExtensionForTests<FeatureFinishExtensionForTests> implements FeatureFinishExtension
{
    @Override
    public Iterable<ExtensionCommand> beforeRebase()
    {
        return Lists.<ExtensionCommand>newArrayList(createExtension(BEFORE_REBASE));
    }

    @Override
    public Iterable<ExtensionCommand> afterRebase()
    {
        return Lists.<ExtensionCommand>newArrayList(createExtension(AFTER_REBASE));
    }

    @Override
    public Iterable<ExtensionCommand> beforeDevelopCheckout()
    {
        return Lists.<ExtensionCommand>newArrayList(createExtension(BEFORE_DEVELOP_CHECKOUT));
    }

    @Override
    public Iterable<ExtensionCommand> afterDevelopCheckout()
    {
        return Lists.<ExtensionCommand>newArrayList(createExtension(AFTER_DEVELOP_CHECKOUT));
    }

    @Override
    public Iterable<ExtensionCommand> beforeDevelopMerge()
    {
        return Lists.<ExtensionCommand>newArrayList(createExtension(BEFORE_DEVELOP_MERGE));
    }

    @Override
    public Iterable<ExtensionCommand> afterDevelopMerge()
    {
        return Lists.<ExtensionCommand>newArrayList(createExtension(AFTER_DEVELOP_MERGE));
    }

    @Override
    public Iterable<ExtensionCommand> afterPush()
    {
        return Lists.<ExtensionCommand>newArrayList(createExtension(AFTER_PUSH));
    }

    @Override
    public Iterable<ExtensionCommand> afterTopicCheckout()
    {
        return Lists.<ExtensionCommand>newArrayList(createExtension(AFTER_TOPIC_CHECKOUT));
    }

    @Override
    public Iterable<ExtensionCommand> beforeTag()
    {
        return Lists.<ExtensionCommand>newArrayList(createExtension(BEFORE_TAG));
    }

    @Override
    public Iterable<ExtensionCommand> afterTag()
    {
        return Lists.<ExtensionCommand>newArrayList(createExtension(AFTER_TAG));
    }
}
