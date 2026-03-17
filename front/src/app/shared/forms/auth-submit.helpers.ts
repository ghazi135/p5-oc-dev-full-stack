import { computed, Signal, WritableSignal } from '@angular/core';
import { AbstractControl, FormGroup } from '@angular/forms';
import { toSignal } from '@angular/core/rxjs-interop';
import { startWith } from 'rxjs';

export function createFormSubmitSignals(
  form: AbstractControl,
  submitting: Signal<boolean>,
) {
  const formStatus$ = form.statusChanges.pipe(startWith(form.status));
  const formStatus = toSignal(formStatus$, { initialValue: form.status });

  const canSubmit = computed(() => formStatus() === 'VALID' && !submitting());

  return { formStatus, canSubmit };
}

/**
 * - reset erreurs
 * - si invalid -> touch + return null
 * - sinon : passe submitting à true + retourne le payload typé
 */
export function prepareSubmit<T>(
  form: FormGroup,
  submitting: WritableSignal<boolean>,
  globalError: WritableSignal<string | null>,
  fieldErrors: WritableSignal<Record<string, string[]> | null>,
): T | null {
  globalError.set(null);
  fieldErrors.set(null);

  if (form.invalid) {
    form.markAllAsTouched();
    return null;
  }

  submitting.set(true);
  return form.getRawValue() as T;
}
