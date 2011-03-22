/*
 * Copyright 2000-2011 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.ide.presentation;

import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.NullableLazyValue;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author Dmitry Avdeev
 */
public class PresentationTemplateImpl implements PresentationTemplate {

  @Override
  @Nullable
  public Icon getIcon(Object o, int flags) {
    PresentationIconProvider iconProvider = myIconProvider.getValue();
    return iconProvider == null ? myIcon.getValue() : iconProvider.getIcon(o, flags);
  }

  @Override
  @Nullable
  public String getTypeName() {
    return StringUtil.isEmpty(myPresentation.typeName()) ? null : myPresentation.typeName();
  }

  @Override
  @Nullable
  public String getName(Object o) {
    PresentationNameProvider namer = myNameProvider.getValue();
    return namer == null ? null : namer.getName(o);
  }

  public PresentationTemplateImpl(Presentation presentation, Class<?> aClass) {
    this.myPresentation = presentation;
    myClass = aClass;
  }

  private final Presentation myPresentation;
  private final Class<?> myClass;

  private final NullableLazyValue<Icon> myIcon = new NullableLazyValue<Icon>() {
    @Override
    protected Icon compute() {
      if (StringUtil.isEmpty(myPresentation.icon())) return null;
      return IconLoader.getIcon(myPresentation.icon(), myClass);
    }
  };

  private final NullableLazyValue<PresentationNameProvider> myNameProvider = new NullableLazyValue<PresentationNameProvider>() {
    @Override
    protected PresentationNameProvider compute() {
      Class<? extends PresentationNameProvider> aClass = myPresentation.nameProviderClass();

      try {
        return aClass == PresentationNameProvider.class ? null : aClass.newInstance();
      }
      catch (Exception e) {
        return null;
      }
    }
  };

  private final NullableLazyValue<PresentationIconProvider> myIconProvider = new NullableLazyValue<PresentationIconProvider>() {
    @Override
    protected PresentationIconProvider compute() {
      Class<? extends PresentationIconProvider> aClass = myPresentation.iconProviderClass();

      try {
        return aClass == PresentationIconProvider.class ? null : aClass.newInstance();
      }
      catch (Exception e) {
        return null;
      }
    }
  };

}
