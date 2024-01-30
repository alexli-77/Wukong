import pandas as pd
import re
import sys

def extract_from_tags(tag, tags):
  search_string = tag + "=[\w]+"
  extracted_tag = re.findall(search_string, tags)[0]
  extracted_tag = re.findall("\=(.*)", extracted_tag)[0]
  if extracted_tag == "true":
    return True
  else:
    return False

def create_final_df(df, cols):
  final_df = pd.DataFrame(columns = cols)
  for index, row in df.iterrows():
    final_df.loc[index, 'Java API'] = row['Java API']
    final_df.loc[index, 'Counts'] = row['Counts']
  return final_df

def find_instrumentation_candidates(final_df, cols, name):
  instrumentation_candidates_df = pd.DataFrame(columns = cols)
  instrumentation_candidates_df =  final_df
  print("output (rows, columns):", instrumentation_candidates_df.shape)
  file_name = name.replace("extracted-methods", "instrumentation-candidates")
  instrumentation_candidates_df.to_csv(r'./' + file_name, index=False)
  print("instrumentation candidates saved in ./" + file_name)

def main(argv):
  try:
    cols = ["Java API", "Counts"]
    df = pd.read_csv(argv[1])
    print("input (rows, columns):", df.shape)
    final_df = create_final_df(df, cols)
    name = argv[1]
    find_instrumentation_candidates(final_df, cols, name)
  except Exception as e:
    print("USAGE: python filter.py </path/to/method/list>.csv")
    print(e)
    sys.exit()

if __name__ == "__main__":
  main(sys.argv)
