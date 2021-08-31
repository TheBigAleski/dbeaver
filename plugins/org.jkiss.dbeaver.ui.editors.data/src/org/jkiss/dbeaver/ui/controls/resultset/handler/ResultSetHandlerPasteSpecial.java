/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2021 DBeaver Corp and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jkiss.dbeaver.ui.controls.resultset.handler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.jkiss.code.NotNull;
import org.jkiss.dbeaver.ui.UIUtils;
import org.jkiss.dbeaver.ui.controls.resultset.IResultSetController;
import org.jkiss.dbeaver.ui.controls.resultset.IResultSetEditor;
import org.jkiss.dbeaver.ui.controls.resultset.IResultSetPresentation;
import org.jkiss.dbeaver.ui.controls.resultset.ResultSetPasteSettings;
import org.jkiss.dbeaver.ui.controls.resultset.internal.ResultSetMessages;
import org.jkiss.dbeaver.ui.dialogs.BaseDialog;

public class ResultSetHandlerPasteSpecial extends ResultSetHandlerMain {
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        final IResultSetController resultSet = getActiveResultSet(HandlerUtil.getActivePart(event));
        if (resultSet == null) {
            return null;
        }
        final IResultSetPresentation presentation = resultSet.getActivePresentation();
        if (!(presentation instanceof IResultSetEditor)) {
            return null;
        }
        final AdvancedPasteConfigDialog configDialog = new AdvancedPasteConfigDialog(HandlerUtil.getActiveShell(event));
        if (configDialog.open() == IDialogConstants.OK_ID) {
            ((IResultSetEditor) presentation).pasteFromClipboard(configDialog.pasteSettings);
        }
        return null;
    }

    private static class AdvancedPasteConfigDialog extends BaseDialog {
        private static final String DIALOG_ID = "AdvancedPasteOptions";
        private static final String PROP_NULLS_INSTEAD_OF_EMPTY_VALUES = "nullsInsteadOfEmptyValues";
        private static final String PROP_SPLIT_ROWS_AT_NEW_LINES = "splitRowsAtNewLines";

        private final IDialogSettings dialogSettings;
        private final ResultSetPasteSettings pasteSettings;

        private Button nullsInsteadOfEmptyValuesCheck;
        private Button splitRowsAtNewLinesCheck;

        public AdvancedPasteConfigDialog(@NotNull Shell shell) {
            super(shell, ResultSetMessages.dialog_paste_as_title, null);
            setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

            this.dialogSettings = UIUtils.getDialogSettings(DIALOG_ID);
            this.pasteSettings = new ResultSetPasteSettings();
            if (dialogSettings.get(PROP_NULLS_INSTEAD_OF_EMPTY_VALUES) != null) {
                pasteSettings.setUseNullsInsteadOfEmptyValues(dialogSettings.getBoolean(PROP_NULLS_INSTEAD_OF_EMPTY_VALUES));
            }
            if (dialogSettings.get(PROP_SPLIT_ROWS_AT_NEW_LINES) != null) {
                pasteSettings.setSplitRowsAtNewLine(dialogSettings.getBoolean(PROP_SPLIT_ROWS_AT_NEW_LINES));
            }
        }

        @Override
        protected Composite createDialogArea(Composite parent) {
            final Composite composite = super.createDialogArea(parent);

            nullsInsteadOfEmptyValuesCheck = UIUtils.createCheckbox(
                composite,
                ResultSetMessages.dialog_paste_as_nulls_instead_of_empty_values_text,
                ResultSetMessages.dialog_paste_as_nulls_instead_of_empty_values_tip,
                pasteSettings.isUseNullsInsteadOfEmptyValues(),
                1
            );

            splitRowsAtNewLinesCheck = UIUtils.createCheckbox(
                composite,
                ResultSetMessages.dialog_paste_as_split_rows_at_new_lines_text,
                ResultSetMessages.dialog_paste_as_split_rows_at_new_lines_tip,
                pasteSettings.isSplitRowsAtNewLine(),
                1
            );

            return composite;
        }

        @Override
        protected void okPressed() {
            pasteSettings.setUseNullsInsteadOfEmptyValues(nullsInsteadOfEmptyValuesCheck.getSelection());
            pasteSettings.setSplitRowsAtNewLine(splitRowsAtNewLinesCheck.getSelection());

            dialogSettings.put(PROP_NULLS_INSTEAD_OF_EMPTY_VALUES, pasteSettings.isUseNullsInsteadOfEmptyValues());
            dialogSettings.put(PROP_SPLIT_ROWS_AT_NEW_LINES, pasteSettings.isSplitRowsAtNewLine());

            super.okPressed();
        }
    }
}
