package ut.com.atlassian.jgitflow.core.testutils;

/*-
 * #%L
 * JGit-Flow core library
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
import com.atlassian.jgitflow.core.extension.HotfixStartExtension;

import com.google.common.collect.Lists;

public class HotfixStartExtensionForTests extends BaseExtensionForTests<HotfixStartExtensionForTests> implements HotfixStartExtension
{
    @Override
    public Iterable<ExtensionCommand> beforeCreateBranch()
    {
        return Lists.<ExtensionCommand>newArrayList(createExtension(BEFORE_CREATE_BRANCH));
    }

    @Override
    public Iterable<ExtensionCommand> afterCreateBranch()
    {
        return Lists.<ExtensionCommand>newArrayList(createExtension(AFTER_CREATE_BRANCH));
    }

    @Override
    public Iterable<ExtensionCommand> afterPush()
    {
        return Lists.<ExtensionCommand>newArrayList(createExtension(AFTER_PUSH));
    }
}
