import struct

from podaci.compression_format import ALG_LZ78, pack_header, unpack_header


def lz78_compress(data: bytes) -> tuple[bytes, int]:
    """Kompresuje niz bajtova LZ78 metodom."""

    dictionary: dict[bytes, int] = {}
    next_index = 1
    current = b""
    tokens: list[tuple[int, int]] = []

    for byte in data:
        candidate = current + bytes([byte])
        if candidate in dictionary:
            current = candidate
            continue

        prefix_index = dictionary.get(current, 0)
        tokens.append((prefix_index, byte))
        dictionary[candidate] = next_index
        next_index += 1
        current = b""

    if current:
        tokens.append((dictionary[current], 256))

    if next_index > 65535:
        raise ValueError("LZ78 recnik je prevelik za 16-bitni zapis.")

    body = bytearray()
    body.extend(pack_header(ALG_LZ78, len(data), len(tokens)))
    for prefix_index, next_byte in tokens:
        body.extend(struct.pack(">HH", prefix_index, next_byte))
    return bytes(body), len(dictionary) + 1


def lz78_decompress(payload: bytes) -> bytes:
    algorithm_id, original_size, token_count, body = unpack_header(payload)
    if algorithm_id != ALG_LZ78:
        raise ValueError("Fajl nije kompresovan LZ78 algoritmom.")

    expected_len = token_count * 4
    if len(body) != expected_len:
        raise ValueError("LZ78 telo fajla nema ocekivanu duzinu.")

    dictionary: dict[int, bytes] = {0: b""}
    next_index = 1
    result = bytearray()

    for offset in range(0, len(body), 4):
        prefix_index, next_byte = struct.unpack(">HH", body[offset : offset + 4])
        if prefix_index not in dictionary:
            raise ValueError(f"Neispravan LZ78 prefiks: {prefix_index}")

        phrase = dictionary[prefix_index]
        if next_byte != 256:
            phrase += bytes([next_byte])

        result.extend(phrase)
        dictionary[next_index] = phrase
        next_index += 1

    output = bytes(result)
    if len(output) != original_size:
        raise ValueError("Dekomprimovana duzina se ne poklapa sa zaglavljem.")
    return output
