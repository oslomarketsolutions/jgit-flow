package com.atlassian.jgitflow.core;

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

import org.eclipse.jgit.api.MergeResult;

/**
 * @since version
 */
public class ReleaseMergeResult
{
    private final MergeResult masterResult;
    private final MergeResult developResult;

    public ReleaseMergeResult(MergeResult masterResult, MergeResult developResult)
    {
        this.masterResult = masterResult;
        this.developResult = developResult;
    }

    public MergeResult getMasterResult()
    {
        return masterResult;
    }

    public MergeResult getDevelopResult()
    {
        return developResult;
    }

    public boolean wasSuccessful()
    {
        return (!masterHasProblems() && !developHasProblems());
    }

    public boolean masterHasProblems()
    {
        return !masterResult.getMergeStatus().isSuccessful();
    }

    public boolean developHasProblems()
    {
        return !developResult.getMergeStatus().isSuccessful();
    }

}
