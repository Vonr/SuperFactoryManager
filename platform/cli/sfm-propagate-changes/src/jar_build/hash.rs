use blake3::Hasher as Blake3Hasher;
use eyre::Context;
use sha1::digest::{DynDigest, Update};
use sha1::{Digest, Sha1 as Sha1Hasher};
use std::path::Path;
use tracing::instrument;

#[derive(Copy, Clone, Debug, PartialEq, Eq, Hash)]
pub enum ContentHashAlgorithm {
    Sha1,
    Blake3,
}
impl core::fmt::Display for ContentHashAlgorithm {
    fn fmt(&self, f: &mut core::fmt::Formatter<'_>) -> core::fmt::Result {
        match self {
            ContentHashAlgorithm::Sha1 => write!(f, "sha1"),
            ContentHashAlgorithm::Blake3 => write!(f, "blake3"),
        }
    }
}

// TODO: convert to blake3
#[derive(Debug, Clone, PartialEq, Eq, Hash, Copy)]
pub struct ContentHash {
    pub value: [u8; 20],
    pub algorithm: ContentHashAlgorithm,
}

impl core::fmt::Display for ContentHash {
    fn fmt(&self, f: &mut core::fmt::Formatter<'_>) -> core::fmt::Result {
        write!(f, "{}:", self.algorithm)?;
        for byte in &self.value {
            write!(f, "{:02x}", byte)?;
        }
        Ok(())
    }
}

impl ContentHash {
    #[instrument(level = "debug", name = "sha1_from_bytes", skip_all)]
    pub fn from_bytes(bytes: &[u8], algorithm: ContentHashAlgorithm) -> Self {
        let value = match algorithm {
            ContentHashAlgorithm::Sha1 => {
                let mut hasher = Sha1Hasher::new();
                Update::update(&mut hasher, bytes);
                let mut array = [0u8; 20];
                DynDigest::finalize_into(hasher, &mut array).unwrap();
                array
            }
            ContentHashAlgorithm::Blake3 => {
                let mut hasher = Blake3Hasher::new();
                hasher.update(bytes);
                let mut result = hasher.finalize_xof();
                let mut array = [0u8; 20];
                result.fill(&mut array);
                array
            }
        };
        Self { value, algorithm }
    }
    #[instrument(level = "debug", name = "sha1_from_path", skip_all)]
    pub fn from_path(
        path: impl AsRef<Path>,
        algorithm: ContentHashAlgorithm,
    ) -> eyre::Result<Self> {
        let path = path.as_ref();
        let bytes =
            std::fs::read(path).wrap_err_with(|| format!("Failed to read {}", path.display()))?;
        Ok(Self::from_bytes(&bytes, algorithm))
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_sha1() {
        let data = b"hello world";
        let hash = ContentHash::from_bytes(data, ContentHashAlgorithm::Sha1);
        assert_eq!(
            hash.to_string(),
            "sha1:2aae6c35c94fcfb415dbe95f408b9ce91ee846ed"
        );
    }

    #[test]
    fn test_blake3() {
        let data = b"hello world";
        let hash = ContentHash::from_bytes(data, ContentHashAlgorithm::Blake3);
        assert_eq!(
            hash.to_string(),
            "blake3:d74981efa70a0c880b8d8c1985d075dbcbf679b9"
        );
    }
}