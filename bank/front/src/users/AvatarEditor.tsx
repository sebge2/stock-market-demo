import { useEffect, useRef, useState } from 'react';
import { ApiError } from '../employees/employees.api';
import { MAX_AVATAR_BYTES, readAsBase64, saveAvatar } from './users.api';

/** Props of the avatar dialog. */
interface AvatarEditorProps {
  /** Identifier of the user account owning the avatar. */
  userId: string;
  /** Initials displayed when there is no avatar. */
  initials: string;
  /** Current avatar (base64 PNG), if any. */
  avatar: string | null;
  /** Called with the stored avatar once it has been saved. */
  onSaved: (avatar: string | null) => void;
  /** Called when the dialog is closed. */
  onClose: () => void;
}

/** Modal dialog used to choose, replace or remove the avatar of an employee. */
export function AvatarEditor({ userId, initials, avatar, onSaved, onClose }: AvatarEditorProps) {
  const dialog = useRef<HTMLDialogElement>(null);
  const input = useRef<HTMLInputElement>(null);
  /** Chosen source image (base64); empty string means removal; null means no pending change. */
  const [pending, setPending] = useState<string | null>(null);
  const [previewType, setPreviewType] = useState('image/png');
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const element = dialog.current;
    if (element && !element.open) {
      element.showModal();
    }
  }, []);

  const shown = pending === null ? avatar : pending;
  const type = pending === null ? 'image/png' : previewType;

  const choose = async (file: File | undefined) => {
    if (!file) {
      return;
    }
    setError(null);
    if (!file.type.startsWith('image/')) {
      setError('Le fichier doit être une image (PNG, JPEG, GIF ou BMP).');
      return;
    }
    if (file.size > MAX_AVATAR_BYTES) {
      setError("L'image dépasse 5 Mo.");
      return;
    }
    setPending(await readAsBase64(file));
    setPreviewType(file.type);
  };

  const save = () => {
    if (pending === null) {
      return;
    }
    setSaving(true);
    setError(null);
    saveAvatar(userId, pending)
      .then((stored) => {
        onSaved(stored);
        onClose();
      })
      .catch((e: Error) => {
        setError(
          e instanceof ApiError && e.status === 403
            ? "Vous n'avez pas la permission de modifier la photo."
            : e instanceof ApiError && e.status === 400
              ? "Cette image n'est pas valide (PNG, JPEG, GIF ou BMP attendu)."
              : e.message,
        );
        setSaving(false);
      });
  };

  return (
    <dialog ref={dialog} className="avatar-dialog" aria-labelledby="avatar-dialog-title" onClose={onClose}>
      <h2 id="avatar-dialog-title">Modifier la photo</h2>
      {error && <div role="alert" className="alert">{error}</div>}
      <div className="avatar-editor-body">
        <div className="profile-avatar">
          {shown ? <img className="avatar-img" src={`data:${type};base64,${shown}`} alt="Aperçu de la photo" /> : initials}
        </div>
        <div className="avatar-editor-actions">
          <input
            ref={input}
            type="file"
            accept="image/png,image/jpeg,image/gif,image/bmp"
            hidden
            aria-label="Choisir une image"
            onChange={(e) => {
              void choose(e.target.files?.[0]);
              e.target.value = '';
            }}
          />
          <button type="button" className="btn-outline" disabled={saving} onClick={() => input.current?.click()}>
            Choisir une image
          </button>
          <button type="button" className="btn-secondary" disabled={saving || !shown} onClick={() => setPending('')}>
            Retirer la photo
          </button>
        </div>
      </div>
      <div className="muted">Réduite automatiquement à 128 × 128 pixels maximum.</div>
      <div className="form-actions">
        <button type="button" className="btn-secondary" disabled={saving} onClick={() => dialog.current?.close()}>
          Annuler
        </button>
        <button type="button" className="btn-primary" disabled={saving || pending === null} onClick={save}>
          {saving ? 'Enregistrement…' : 'Enregistrer'}
        </button>
      </div>
    </dialog>
  );
}
